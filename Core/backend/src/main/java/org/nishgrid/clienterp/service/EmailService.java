package org.nishgrid.clienterp.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.nishgrid.clienterp.model.Customer;
import org.nishgrid.clienterp.model.EmailLog;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired private JavaMailSender javaMailSender;
    @Autowired private EmailLogRepository emailLogRepository;

    @Async
    public void sendInvoiceEmail(SalesInvoice invoice, String subject, String body) {
        Customer customer = (invoice != null) ? invoice.getCustomer() : null;

        if (customer == null || customer.getEmail() == null || customer.getEmail().isBlank()) {
            logger.warn("Attempted to send email for an invoice with no customer or a blank email address. Invoice ID: {}", (invoice != null ? invoice.getInvoiceId() : "N/A"));
            return;
        }

        EmailLog log = new EmailLog();
        log.setSalesInvoice(invoice);
        log.setCustomer(customer);
        log.setEmailAddress(customer.getEmail());
        log.setSentTime(LocalDateTime.now());
        log.setStatus(EmailLog.EmailStatus.PENDING);
        emailLogRepository.save(log);

        try {
            logger.info("Attempting to send invoice email to: {}", customer.getEmail());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(customer.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(mimeMessage);

            log.setStatus(EmailLog.EmailStatus.SENT);
            emailLogRepository.save(log);
            logger.info("Invoice email successfully sent to: {}", customer.getEmail());

        } catch (Exception e) {
            log.setStatus(EmailLog.EmailStatus.FAILED);
            log.setErrorMessage(e.getMessage());
            emailLogRepository.save(log);
            logger.error("Failed to send invoice email to {}. Error: {}", customer.getEmail(), e.getMessage(), e);
        }
    }

    @Async
    public void sendInvoiceEmail(SalesInvoice invoice, String subject, String body, byte[] pdfBytes, String attachmentName) {
        Customer customer = (invoice != null) ? invoice.getCustomer() : null;

        if (customer == null || customer.getEmail() == null || customer.getEmail().isBlank()) {
            logger.warn("Attempted to send email for an invoice with no customer or a blank email address. Invoice ID: {}", (invoice != null ? invoice.getInvoiceId() : "N/A"));
            return;
        }

        EmailLog log = new EmailLog();
        log.setSalesInvoice(invoice);
        log.setCustomer(customer);
        log.setEmailAddress(customer.getEmail());
        log.setSentTime(LocalDateTime.now());
        log.setStatus(EmailLog.EmailStatus.PENDING);
        emailLogRepository.save(log);

        try {
            logger.info("Attempting to send invoice email with attachment to: {}", customer.getEmail());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(customer.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.addAttachment(attachmentName, new ByteArrayDataSource(pdfBytes, "application/pdf"));

            javaMailSender.send(mimeMessage);

            log.setStatus(EmailLog.EmailStatus.SENT);
            emailLogRepository.save(log);
            logger.info("Invoice email with attachment successfully sent to: {}", customer.getEmail());

        } catch (Exception e) {
            log.setStatus(EmailLog.EmailStatus.FAILED);
            log.setErrorMessage(e.getMessage());
            emailLogRepository.save(log);
            logger.error("Failed to send invoice email with attachment to {}. Error: {}", customer.getEmail(), e.getMessage(), e);
        }
    }
}
