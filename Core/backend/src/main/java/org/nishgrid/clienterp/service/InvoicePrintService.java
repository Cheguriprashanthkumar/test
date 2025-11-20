package org.nishgrid.clienterp.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.model.BankDetails;
import org.nishgrid.clienterp.model.Customer;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.model.SalesItem;
import org.nishgrid.clienterp.model.CompanyDetails;
import org.nishgrid.clienterp.repository.BankDetailsRepository;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class InvoicePrintService {

    private static final Logger logger = LoggerFactory.getLogger(InvoicePrintService.class);

    @Autowired
    private SalesInvoiceRepository invoiceRepository;
    @Autowired
    private BankDetailsRepository bankDetailsRepository;
    @Autowired
    private CompanyDetailsService companyDetailsService;

    private static final int DEFAULT_PADDING = 5;
    private static final int SMALL_PADDING = 3;

    private static final Color COLOR_PRIMARY_TEXT = new Color(18, 18, 18);
    private static final Color COLOR_SECONDARY_TEXT = new Color(100, 100, 100);
    private static final Color COLOR_ACCENT = new Color(128, 0, 0);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color COLOR_GRAND_TOTAL_BG = new Color(230, 230, 230);
    private static final Color COLOR_DEDUCTION = new Color(178, 34, 34);

    private static final Font FONT_COMPANY_NAME = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, COLOR_ACCENT);
    private static final Font FONT_COMPANY_DETAILS = FontFactory.getFont(FontFactory.HELVETICA, 9, COLOR_PRIMARY_TEXT);
    private static final Font FONT_TERMS = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_SECONDARY_TEXT);
    private static final Font FONT_INVOICE_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOR_PRIMARY_TEXT);
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_PRIMARY_TEXT);
    private static final Font FONT_BODY_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_PRIMARY_TEXT);
    private static final Font FONT_TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, COLOR_PRIMARY_TEXT);
    private static final Font FONT_GRAND_TOTAL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_PRIMARY_TEXT);
    private static final Font FONT_DEDUCTION_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_DEDUCTION);

    private static final DecimalFormat DF = new DecimalFormat("#,##,##0.00");
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private BigDecimal toBigDecimal(Double value) {
        return Optional.ofNullable(value).map(Object::toString).map(BigDecimal::new).orElse(BigDecimal.ZERO);
    }

    private String formatNumber(Object value) {
        if (value == null) return "0.00";
        if (value instanceof BigDecimal bd) return DF.format(bd.setScale(2, RoundingMode.HALF_UP));
        if (value instanceof Number num) return DF.format(num.doubleValue());
        try {
            return DF.format(new BigDecimal(value.toString()));
        } catch (Exception e) {
            return "0.00";
        }
    }

    private String safeString(String value) {
        return Optional.ofNullable(value).orElse("");
    }

    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long invoiceId) {
        SalesInvoice invoice = invoiceRepository.findByIdWithItems(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Estimated number not found: " + invoiceId));
        return generatePdfForInvoice(invoice);
    }

    @Transactional(readOnly = true)
    public byte[] generatePdfForInvoice(SalesInvoice invoice) {
        BankDetails bankDetail = bankDetailsRepository.findByStatus(BankDetails.Status.Active).stream().findFirst().orElse(null);
        CompanyDetails companyDetails = companyDetailsService.getActiveCompanyDetails()
                .orElseThrow(() -> new EntityNotFoundException("No active company details found. Please configure company details."));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            addHeader(document, companyDetails);
            addCustomerAndInvoiceDetails(document, invoice);
            addItemsTable(document, invoice);
            addFooter(document, invoice, bankDetail, companyDetails);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            logger.error("Error generating PDF for invoice {}: {}", invoice.getInvoiceId(), e.getMessage(), e);
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addHeader(Document document, CompanyDetails details) throws DocumentException {
        Paragraph headerParagraph = new Paragraph();
        headerParagraph.setAlignment(Element.ALIGN_CENTER);

        headerParagraph.add(new Chunk(safeString(details.getCompanyName()), FONT_COMPANY_NAME));
        headerParagraph.add(Chunk.NEWLINE);
        headerParagraph.add(new Chunk(safeString(details.getCompanyTagline()), FONT_COMPANY_DETAILS));
        headerParagraph.add(Chunk.NEWLINE);
        headerParagraph.add(new Chunk(safeString(details.getCompanyAddress()), FONT_COMPANY_DETAILS));
        headerParagraph.add(Chunk.NEWLINE);

        if (details.getCompanyContacts() != null && !details.getCompanyContacts().isEmpty()) {
            String contacts = String.join(", ", details.getCompanyContacts());
            headerParagraph.add(new Chunk(contacts, FONT_COMPANY_DETAILS));
        } else {
            headerParagraph.add(new Chunk("", FONT_COMPANY_DETAILS));
        }

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        headerTable.setSpacingAfter(15);
        headerTable.addCell(headerParagraph);
        document.add(headerTable);
    }

    private void addCustomerAndInvoiceDetails(Document document, SalesInvoice invoice) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(new float[]{6, 4});
        mainTable.setWidthPercentage(100);
        mainTable.setSpacingAfter(15);

        PdfPTable customerInfoTable = new PdfPTable(new float[]{1.5f, 4.5f});
        customerInfoTable.setWidthPercentage(100);

        PdfPCell customerContainerCell = new PdfPCell(customerInfoTable);
        customerContainerCell.setBorder(Rectangle.BOX);
        customerContainerCell.setBorderColor(COLOR_BORDER);
        customerContainerCell.setPadding(0);

        customerInfoTable.addCell(createBorderedInnerCell("Bill To:", Element.ALIGN_LEFT, FONT_BODY_BOLD, 2));
        Customer customer = invoice.getCustomer();
        if (customer != null) {
            customerInfoTable.addCell(createBorderedInnerCell(customer.getName(), Element.ALIGN_LEFT, FONT_BODY_BOLD, 2));
            customerInfoTable.addCell(createBorderedInnerCell(customer.getAddress() != null ? customer.getAddress() : "N/A", Element.ALIGN_LEFT, FONT_BODY, 2));
            customerInfoTable.addCell(createBorderedInnerCell("Mobile:", Element.ALIGN_LEFT, FONT_BODY));
            customerInfoTable.addCell(createBorderedInnerCell(customer.getMobile(), Element.ALIGN_LEFT, FONT_BODY_BOLD));
        }
        mainTable.addCell(customerContainerCell);

        PdfPTable invoiceInfoTable = new PdfPTable(2);
        invoiceInfoTable.setWidthPercentage(100);

        PdfPCell invoiceContainerCell = new PdfPCell(invoiceInfoTable);
        invoiceContainerCell.setBorder(Rectangle.BOX);
        invoiceContainerCell.setBorderColor(COLOR_BORDER);
        invoiceContainerCell.setPadding(0);

        PdfPCell titleCell = createInnerCell("Rough Estimate", Element.ALIGN_CENTER, FONT_INVOICE_TITLE, 2);
        titleCell.setBackgroundColor(COLOR_TABLE_HEADER_BG);
        titleCell.setBorder(Rectangle.BOX);
        titleCell.setBorderColor(COLOR_BORDER);
        titleCell.setPadding(8);
        invoiceInfoTable.addCell(titleCell);

        invoiceInfoTable.addCell(createBorderedInnerCell("Estimate No:", Element.ALIGN_LEFT, FONT_BODY));
        invoiceInfoTable.addCell(createBorderedInnerCell(String.valueOf(invoice.getInvoiceNo()), Element.ALIGN_RIGHT, FONT_BODY_BOLD));
        invoiceInfoTable.addCell(createBorderedInnerCell("Date:", Element.ALIGN_LEFT, FONT_BODY));
        invoiceInfoTable.addCell(createBorderedInnerCell(invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), Element.ALIGN_RIGHT, FONT_BODY_BOLD));

        mainTable.addCell(invoiceContainerCell);
        document.add(mainTable);
    }

    private void addItemsTable(Document document, SalesInvoice invoice) throws DocumentException {
        boolean hasDiamondItems = invoice.getSalesItems().stream()
                .anyMatch(item -> Optional.ofNullable(item.getDiamondCarat()).orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0);
        float[] columnWidths;
        if (hasDiamondItems) {
            columnWidths = new float[]{0.6f, 3f, 0.8f, 1f, 1f, 1f, 1f, 1f, 1f, 1.2f};
        } else {
            columnWidths = new float[]{0.6f, 3f, 0.8f, 1f, 1f, 1f, 1f, 1.2f};
        }
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setHeaderRows(2);
        table.getDefaultCell().setBorderColor(COLOR_BORDER);
        table.addCell(createHeaderCell("Sr. No.", 2));
        table.addCell(createHeaderCell("Item Description", 2));
        table.addCell(createHeaderCell("Purity", 2));
        table.addCell(createHeaderCell("Weight (g)", 1, 2));
        if (hasDiamondItems) {
            table.addCell(createHeaderCell("Diamond Carat", 2));
            table.addCell(createHeaderCell("Diamond Rate", 2));
        }
        table.addCell(createHeaderCell("Gold Rate", 2));
        table.addCell(createHeaderCell("Making charges", 2));
        table.addCell(createHeaderCell("Total", 2));
        table.addCell(createHeaderCell("Gross"));
        table.addCell(createHeaderCell("Net"));
        int serialNumber = 1;
        BigDecimal totalGrossWeight = BigDecimal.ZERO;
        BigDecimal totalNetWeight = BigDecimal.ZERO;
        for (SalesItem item : invoice.getSalesItems()) {
            table.addCell(createBodyCell(String.valueOf(serialNumber++), Element.ALIGN_CENTER));
            table.addCell(createBodyCell(item.getItemName(), Element.ALIGN_LEFT));
            table.addCell(createBodyCell(item.getPurity(), Element.ALIGN_CENTER));
            table.addCell(createBodyCell(toBigDecimal(item.getGrossWeight()).setScale(3, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT));
            table.addCell(createBodyCell(toBigDecimal(item.getNetWeight()).setScale(3, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT));
            if (hasDiamondItems) {
                String diamondCarat = (item.getDiamondCarat() != null && item.getDiamondCarat().compareTo(BigDecimal.ZERO) > 0) ? item.getDiamondCarat().setScale(2, RoundingMode.HALF_UP).toString() : "N/A";
                String diamondRate = (item.getDiamondRate() != null && item.getDiamondRate().compareTo(BigDecimal.ZERO) > 0) ? formatNumber(item.getDiamondRate()) : "N/A";
                table.addCell(createBodyCell(diamondCarat, Element.ALIGN_RIGHT));
                table.addCell(createBodyCell(diamondRate, Element.ALIGN_RIGHT));
            }
            table.addCell(createBodyCell(formatNumber(item.getRatePerGram()), Element.ALIGN_RIGHT));
            BigDecimal makingChargeValue = Optional.ofNullable(item.getMakingChargeAmount()).orElse(BigDecimal.ZERO);
            String makingChargeDisplay = makingChargeValue.compareTo(BigDecimal.ZERO) > 0 ? formatNumber(makingChargeValue) : formatNumber(item.getMakingCharge()) + "%";
            table.addCell(createBodyCell(makingChargeDisplay, Element.ALIGN_RIGHT));
            table.addCell(createBodyCell(formatNumber(item.getTotalPrice()), Element.ALIGN_RIGHT));
            totalGrossWeight = totalGrossWeight.add(toBigDecimal(item.getGrossWeight()));
            totalNetWeight = totalNetWeight.add(toBigDecimal(item.getNetWeight()));
        }
        PdfPCell totalWeightLabelCell = createBodyCell("Total Weight (g)", Element.ALIGN_RIGHT, FONT_BODY_BOLD);
        totalWeightLabelCell.setColspan(hasDiamondItems ? 5 : 3);
        totalWeightLabelCell.setBorder(Rectangle.TOP);
        totalWeightLabelCell.setBorderColor(COLOR_PRIMARY_TEXT);
        table.addCell(totalWeightLabelCell);
        PdfPCell totalGrossCell = createBodyCell(totalGrossWeight.setScale(3, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, FONT_BODY_BOLD);
        totalGrossCell.setBorder(Rectangle.TOP);
        totalGrossCell.setBorderColor(COLOR_PRIMARY_TEXT);
        table.addCell(totalGrossCell);
        PdfPCell totalNetCell = createBodyCell(totalNetWeight.setScale(3, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT, FONT_BODY_BOLD);
        totalNetCell.setBorder(Rectangle.TOP);
        totalNetCell.setBorderColor(COLOR_PRIMARY_TEXT);
        table.addCell(totalNetCell);
        PdfPCell emptyFooterCell = createBodyCell("", Element.ALIGN_LEFT);
        emptyFooterCell.setColspan(hasDiamondItems ? 4 : 2);
        emptyFooterCell.setBorder(Rectangle.TOP);
        emptyFooterCell.setBorderColor(COLOR_PRIMARY_TEXT);
        table.addCell(emptyFooterCell);
        document.add(table);
    }

    private void addFooter(Document document, SalesInvoice invoice, BankDetails bankDetail, CompanyDetails details) throws DocumentException, IOException {

        PdfPTable mainFooterTable = new PdfPTable(new float[]{6, 4});
        mainFooterTable.setWidthPercentage(100);
        mainFooterTable.setSpacingBefore(10);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPaddingRight(10);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);
        leftCell.setPadding(0);

        BigDecimal finalTotal = invoice.getNetAmount();
        long totalAmountInLong = finalTotal.longValue();
        Paragraph amountInWords = new Paragraph("Estimated Amount (In Words): ", FONT_BODY);
        amountInWords.add(new Chunk(NumberToWordsConverter.convert(totalAmountInLong) + " Only.", FONT_BODY_BOLD));
        amountInWords.setSpacingBefore(DEFAULT_PADDING);
        amountInWords.setSpacingAfter(DEFAULT_PADDING);
        leftCell.addElement(amountInWords);

        if (bankDetail != null) {
            PdfPTable bankAndQrTable = new PdfPTable(new float[]{3.5f, 1.5f});
            bankAndQrTable.setWidthPercentage(100);

            PdfPTable bankTable = new PdfPTable(new float[]{1.5f, 3.5f});
            bankTable.setWidthPercentage(100);

            bankTable.addCell(createBorderedInnerCell("Bank Details:", Element.ALIGN_LEFT, FONT_BODY_BOLD, 2));
            bankTable.addCell(createBorderedInnerCell("A/c Name:", Element.ALIGN_LEFT, FONT_BODY));
            bankTable.addCell(createBorderedInnerCell(bankDetail.getAccountName(), Element.ALIGN_LEFT, FONT_BODY_BOLD));
            bankTable.addCell(createBorderedInnerCell("Bank:", Element.ALIGN_LEFT, FONT_BODY));
            bankTable.addCell(createBorderedInnerCell(bankDetail.getBankName(), Element.ALIGN_LEFT, FONT_BODY_BOLD));
            bankTable.addCell(createBorderedInnerCell("A/c No:", Element.ALIGN_LEFT, FONT_BODY));
            bankTable.addCell(createBorderedInnerCell(bankDetail.getAccountNumber(), Element.ALIGN_LEFT, FONT_BODY_BOLD));
            bankTable.addCell(createBorderedInnerCell("IFSC:", Element.ALIGN_LEFT, FONT_BODY));
            bankTable.addCell(createBorderedInnerCell(bankDetail.getIfscCode(), Element.ALIGN_LEFT, FONT_BODY_BOLD));

            PdfPCell bankTableContainer = new PdfPCell(bankTable);
            bankTableContainer.setPadding(0);
            bankTableContainer.setBorder(Rectangle.BOX);
            bankTableContainer.setBorderColor(COLOR_BORDER);

            bankAndQrTable.addCell(bankTableContainer);

            PdfPCell qrCell = new PdfPCell();
            qrCell.setBorder(Rectangle.BOX);
            qrCell.setBorderColor(COLOR_BORDER);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            qrCell.setMinimumHeight(80);
            qrCell.setPadding(5);

            if (bankDetail.getQrCodeData() != null && bankDetail.getQrCodeData().length > 0) {
                try {
                    Image qrCodeImage = Image.getInstance(bankDetail.getQrCodeData());
                    qrCodeImage.scaleToFit(75, 75);
                    qrCell.addElement(qrCodeImage);
                } catch (Exception e) {
                    logger.error("Could not load QR code from byte array for bankId: {}", bankDetail.getBankId(), e);
                    qrCell.addElement(new Phrase("QR N/A", FONT_BODY));
                }
            } else {
                qrCell.addElement(new Phrase("QR N/A", FONT_BODY));
            }
            bankAndQrTable.addCell(qrCell);

            leftCell.addElement(bankAndQrTable);
        } else {
            leftCell.addElement(new Paragraph("Bank details not available.", FONT_BODY));
        }

        Paragraph termsHeader = new Paragraph("Terms & Conditions:", FONT_BODY_BOLD);
        termsHeader.setSpacingBefore(10);
        leftCell.addElement(termsHeader);

        leftCell.addElement(new Phrase("1. This is a rough estimate; final amount may vary based on the actual weight, purity, and prevailing gold rate on the date of billing.", FONT_TERMS));
        leftCell.addElement(Chunk.NEWLINE);
        leftCell.addElement(new Phrase("2. Gold rate and making charges are subject to change as per the market rate.", FONT_TERMS));
        leftCell.addElement(Chunk.NEWLINE);
        leftCell.addElement(new Phrase("3. GST and other applicable taxes will be charged as per government norms at the time of final invoice.", FONT_TERMS));
        leftCell.addElement(Chunk.NEWLINE);
        leftCell.addElement(new Phrase("4. Any modification in design, size, or customization may result in a change in the final price.", FONT_TERMS));
        leftCell.addElement(Chunk.NEWLINE);
        leftCell.addElement(new Phrase("5. This estimate is valid for 24 hours from the date of issue unless specified otherwise.", FONT_TERMS));
        leftCell.addElement(Chunk.NEWLINE);
        leftCell.addElement(new Phrase("6. E.&O.E. (Errors and Omissions Excepted)", FONT_TERMS));

        mainFooterTable.addCell(leftCell);

        PdfPTable totalsTable = new PdfPTable(new float[]{2, 1.5f});
        totalsTable.setWidthPercentage(100);

        totalsTable.addCell(createTotalsCell("Total:-", FONT_BODY_BOLD, Element.ALIGN_RIGHT));
        totalsTable.addCell(createTotalsCell(formatNumber(invoice.getTotalAmount()), FONT_BODY_BOLD, Element.ALIGN_RIGHT));

        if (invoice.getDiscount() != null && invoice.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalsCell("Discount:-", FONT_BODY, Element.ALIGN_RIGHT));
            totalsTable.addCell(createTotalsCell("-" + formatNumber(invoice.getDiscount()), FONT_BODY_BOLD, Element.ALIGN_RIGHT));
        }

        if (invoice.getOldGoldValue() != null && invoice.getOldGoldValue().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalsCell("Old Gold Value:-", FONT_DEDUCTION_BOLD, Element.ALIGN_RIGHT));
            totalsTable.addCell(createTotalsCell("-" + formatNumber(invoice.getOldGoldValue()), FONT_DEDUCTION_BOLD, Element.ALIGN_RIGHT));
        }

        if (invoice.getRoundOff() != null && invoice.getRoundOff().abs().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalsCell("Round Off:-", FONT_BODY, Element.ALIGN_RIGHT));
            totalsTable.addCell(createTotalsCell(formatNumber(invoice.getRoundOff().setScale(2, RoundingMode.HALF_UP)), FONT_BODY, Element.ALIGN_RIGHT));
        }

        totalsTable.addCell(createTotalsCell("Grand Total:-", FONT_GRAND_TOTAL, Element.ALIGN_RIGHT, COLOR_GRAND_TOTAL_BG));
        totalsTable.addCell(createTotalsCell(formatNumber(invoice.getNetAmount()), FONT_GRAND_TOTAL, Element.ALIGN_RIGHT, COLOR_GRAND_TOTAL_BG));

        totalsTable.addCell(createTotalsCell("Amount Paid:-", FONT_BODY_BOLD, Element.ALIGN_RIGHT, COLOR_TABLE_HEADER_BG));
        totalsTable.addCell(createTotalsCell(formatNumber(invoice.getPaidAmount()), FONT_BODY_BOLD, Element.ALIGN_RIGHT, COLOR_TABLE_HEADER_BG));

        totalsTable.addCell(createTotalsCell("Balance Due:-", FONT_BODY_BOLD, Element.ALIGN_RIGHT, COLOR_TABLE_HEADER_BG));
        totalsTable.addCell(createTotalsCell(formatNumber(invoice.getDueAmount()), FONT_BODY_BOLD, Element.ALIGN_RIGHT, COLOR_TABLE_HEADER_BG));

        PdfPCell totalsContainer = new PdfPCell(totalsTable);
        totalsContainer.setBorder(Rectangle.BOX);
        totalsContainer.setBorderColor(COLOR_BORDER);
        totalsContainer.setPadding(0);
        totalsContainer.setVerticalAlignment(Element.ALIGN_TOP);

        mainFooterTable.addCell(totalsContainer);
        document.add(mainFooterTable);

        PdfPTable signatureTable = new PdfPTable(2);
        signatureTable.setWidthPercentage(100);
        signatureTable.setSpacingBefore(50);

        PdfPCell customerSignCell = new PdfPCell(new Phrase("Customer Signature", FONT_BODY_BOLD));
        customerSignCell.setBorder(Rectangle.TOP);
        customerSignCell.setBorderColor(COLOR_PRIMARY_TEXT);
        customerSignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        customerSignCell.setPaddingTop(5);
        signatureTable.addCell(customerSignCell);

        PdfPCell authSignCell = new PdfPCell();
        authSignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        authSignCell.setBorder(Rectangle.TOP);
        authSignCell.setBorderColor(COLOR_PRIMARY_TEXT);
        authSignCell.setPaddingTop(5);
        authSignCell.addElement(new Phrase("For " + safeString(details.getCompanyName()), FONT_BODY_BOLD));
        authSignCell.addElement(new Phrase("Authorised Signatory", FONT_TERMS));
        signatureTable.addCell(authSignCell);

        document.add(signatureTable);
    }

    private PdfPCell createHeaderCell(String content) { return createHeaderCell(content, 1, 1); }
    private PdfPCell createHeaderCell(String content, int rowspan) { return createHeaderCell(content, rowspan, 1); }
    private PdfPCell createHeaderCell(String content, int rowspan, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FONT_TABLE_HEADER));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(COLOR_TABLE_HEADER_BG);
        cell.setBorderColor(COLOR_BORDER);
        cell.setRowspan(rowspan);
        cell.setColspan(colspan);
        cell.setPadding(DEFAULT_PADDING);
        return cell;
    }

    private PdfPCell createBodyCell(String content, int alignment) { return createBodyCell(content, alignment, FONT_BODY); }
    private PdfPCell createBodyCell(String content, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(COLOR_BORDER);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell createTotalsCell(String content, Font font, int alignment, Color bgColor) {
        PdfPCell cell = createTotalsCell(content, font, alignment);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(DEFAULT_PADDING);
        return cell;
    }

    private PdfPCell createTotalsCell(String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(DEFAULT_PADDING);
        return cell;
    }

    private PdfPCell createInnerCell(String content, int alignment, Font font) { return createInnerCell(content, alignment, font, 1); }
    private PdfPCell createInnerCell(String content, int alignment, Font font, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setColspan(colspan);
        cell.setPadding(SMALL_PADDING);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell createBorderedInnerCell(String content, int alignment, Font font) { return createBorderedInnerCell(content, alignment, font, 1); }
    private PdfPCell createBorderedInnerCell(String content, int alignment, Font font, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setColspan(colspan);
        cell.setPadding(SMALL_PADDING);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(COLOR_BORDER);
        return cell;
    }

    public static class NumberToWordsConverter {
        private static final String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        private static final String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        public static String convert(final long n) {
            if (n < 0) return "Minus " + convert(-n);
            if (n < 20) return units[(int) n];
            if (n < 100) return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " : "") + units[(int) (n % 10)];
            if (n < 1000) return units[(int) (n / 100)] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
            if (n < 100000) return convert(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
            if (n < 10000000) return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
            return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
        }
    }
}