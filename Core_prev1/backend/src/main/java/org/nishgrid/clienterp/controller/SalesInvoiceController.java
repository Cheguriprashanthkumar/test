package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.*;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.model.SalesItem;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.nishgrid.clienterp.repository.SalesItemRepository;
import org.nishgrid.clienterp.repository.SalesReturnRepository;
import org.nishgrid.clienterp.service.InvoicePrintService;
import org.nishgrid.clienterp.service.PaymentService;
import org.nishgrid.clienterp.service.ReturnService;
import org.nishgrid.clienterp.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class SalesInvoiceController {

    @Autowired
    private SaleService saleService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private InvoicePrintService printService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private SalesInvoiceRepository salesInvoiceRepository;
    @Autowired
    private SalesItemRepository salesItemRepository;
    @Autowired
    private SalesReturnRepository salesReturnRepository;

    @GetMapping
    public List<InvoiceRowDTO> getInvoices(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status) {
        return saleService.searchInvoicesAsDTOs(search, status);
    }

    @GetMapping("/selection")
    public List<InvoiceSelectionDTO> getInvoicesForSelection(@RequestParam("status") List<String> statuses) {
        return salesInvoiceRepository.findByStatusInIgnoreCase(statuses).stream()
                .map(inv -> new InvoiceSelectionDTO(inv.getInvoiceId(), inv.getInvoiceNo()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{invoiceId}/items")
    public ResponseEntity<List<SalesItemSelectionDTO>> getItemsForInvoice(@PathVariable("invoiceId") Long invoiceId) {
        List<SalesItemSelectionDTO> items = saleService.getItemsForInvoice(invoiceId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{invoiceId}/items/not-returned")
    public ResponseEntity<ApiResponse<List<SalesItemSelectionDTO>>> getNotReturnedItems(@PathVariable("invoiceId") Long invoiceId) {
        List<SalesItem> allItems = salesItemRepository.findByInvoice_InvoiceId(invoiceId);

        List<SalesItemSelectionDTO> returnableItems = allItems.stream()
                .map(item -> {
                    BigDecimal returnedQty = salesReturnRepository.findTotalReturnedQuantityBySalesItemId(item.getSalesItemId())
                            .orElse(BigDecimal.ZERO);

                    BigDecimal originalQty = BigDecimal.valueOf(item.getQuantity());

                    BigDecimal remaining = originalQty.subtract(returnedQty);

                    if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                        SalesItemSelectionDTO dto = new SalesItemSelectionDTO();
                        dto.setId(item.getSalesItemId());
                        dto.setName(item.getItemName());
                        dto.setQuantityRemaining(remaining.intValue());
                        dto.setUnitPrice(item.getRatePerGram());
                        return dto;
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true,
                returnableItems.isEmpty() ? "No returnable items for this invoice" : "Returnable items fetched successfully",
                returnableItems));
    }

    @PostMapping("/{invoiceId}/payments")
    public ResponseEntity<ApiResponse<String>> addPayment(@PathVariable("invoiceId") Long invoiceId,
                                                          @RequestBody PaymentRequestDTO dto) {
        try {
            paymentService.addPayment(invoiceId, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Payment added successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, "Error adding payment: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{invoiceId}/returns")
    public ResponseEntity<ApiResponse<InvoiceRowDTO>> processReturn(@PathVariable("invoiceId") Long invoiceId,
                                                                    @RequestBody ReturnRequestDTO dto) {
        try {
            dto.setInvoiceId(invoiceId);
            returnService.processReturn(dto);

            SalesInvoice updatedInvoice = salesInvoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found after return"));

            InvoiceRowDTO invoiceDto = convertToDto(updatedInvoice);

            return ResponseEntity.ok(new ApiResponse<>(true, "Return processed successfully", invoiceDto));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, "Error processing return: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{invoiceId}/print")
    public ResponseEntity<byte[]> printInvoice(@PathVariable("invoiceId") Long invoiceId) {
        byte[] pdfBytes = printService.generateInvoicePdf(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "invoice-" + invoiceId + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    private InvoiceRowDTO convertToDto(SalesInvoice si) {
        InvoiceRowDTO dto = new InvoiceRowDTO();
        dto.setId(si.getInvoiceId());
        dto.setInvoiceNo(si.getInvoiceNo());
        if (si.getCustomer() != null) {
            dto.setCustomerName(si.getCustomer().getName());
        }
        dto.setInvoiceDate(si.getInvoiceDate());
        dto.setSalesType(si.getSalesType());
        dto.setPaymentMode(si.getPaymentMode());
        dto.setTotalAmount(si.getTotalAmount());
        dto.setNetAmount(si.getNetAmount());
        dto.setStatus(si.getStatus());
        dto.setPaidAmount(si.getPaidAmount());
        dto.setDueAmount(si.getDueAmount());
        dto.setOldGoldValue(si.getOldGoldValue()); // This line is the fix
        return dto;
    }

    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}