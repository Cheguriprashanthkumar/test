package org.nishgrid.clienterp.controller.api;

import org.nishgrid.clienterp.dto.SmsLogDTO;
import org.nishgrid.clienterp.model.SmsNotification;
import org.nishgrid.clienterp.repository.SmsNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sms-logs")
public class SmsLogController {
    @Autowired
    private SmsNotificationRepository smsLogRepository;

    @GetMapping
    public List<SmsLogDTO> getAllSmsLogs() {
        return smsLogRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private SmsLogDTO convertToDto(SmsNotification log) {
        SmsLogDTO dto = new SmsLogDTO();
        dto.setMessageId(log.getMessageId());
        dto.setMessageType(log.getMessageType());
        dto.setStatus(log.getStatus());
        dto.setTimestamp(log.getTimestamp());
        if (log.getCustomer() != null) {
            dto.setCustomerId(log.getCustomer().getCustomerId());
        }
        return dto;
    }
}