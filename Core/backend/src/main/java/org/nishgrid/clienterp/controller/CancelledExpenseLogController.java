package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.CancelledExpenseLogResponse;
import org.nishgrid.clienterp.repository.CancelledExpenseLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/other-expenses/cancelled-logs")
public class CancelledExpenseLogController {

    @Autowired
    private CancelledExpenseLogRepository cancelledExpenseLogRepository;

    @GetMapping
    public List<CancelledExpenseLogResponse> getAllCancelledExpenseLogs() {
        return cancelledExpenseLogRepository.findAllWithDetails().stream()
                .map(CancelledExpenseLogResponse::fromEntity)
                .collect(Collectors.toList());
    }
}