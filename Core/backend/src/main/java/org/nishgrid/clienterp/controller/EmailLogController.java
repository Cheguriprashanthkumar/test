package org.nishgrid.clienterp.controller.api;

import org.nishgrid.clienterp.dto.EmailLogDTO;
import org.nishgrid.clienterp.model.EmailLog;
import org.nishgrid.clienterp.repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/email-logs")
public class EmailLogController {

    @Autowired
    private EmailLogRepository emailLogRepository;

    @GetMapping
    public List<EmailLogDTO> getAllEmailLogs() {
        return emailLogRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private EmailLogDTO convertToDto(EmailLog log) {
        EmailLogDTO dto = new EmailLogDTO();
        dto.setEmailId(log.getEmailId());
        dto.setEmailAddress(log.getEmailAddress());
        dto.setStatus(log.getStatus() != null ? log.getStatus().toString() : null);
        dto.setSentTime(log.getSentTime());
        dto.setErrorMessage(log.getErrorMessage());

        if (log.getCustomer() != null) {
            dto.setCustomerId(log.getCustomer().getCustomerId());
        }
        if (log.getSalesInvoice() != null) {
            dto.setInvoiceId(log.getSalesInvoice().getInvoiceId());
        }
        return dto;
    }
}