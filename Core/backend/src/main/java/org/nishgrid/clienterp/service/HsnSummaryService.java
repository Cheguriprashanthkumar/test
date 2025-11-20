package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.nishgrid.clienterp.dto.HsnAggregationDTO;
import org.nishgrid.clienterp.model.HsnSummary;
import org.nishgrid.clienterp.repository.HsnSummaryRepository;
import org.nishgrid.clienterp.repository.SalesItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HsnSummaryService {

    @Autowired private SalesItemRepository salesItemRepository;
    @Autowired private HsnSummaryRepository hsnSummaryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0 15 1 1 * *")
    @Transactional
    public void generateMonthlyHsnSummary() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        this.generateMonthlyHsnSummary(lastMonth.getYear(), lastMonth.getMonthValue());
    }

    @Transactional
    public void generateMonthlyHsnSummary(Integer year, Integer month) {
        entityManager.clear();

        String monthYear = String.format("%d-%02d", year, month);
        List<HsnAggregationDTO> summaries = salesItemRepository.getHsnSummaryForMonth(year, month);

        for (HsnAggregationDTO dto : summaries) {
            HsnSummary summary = new HsnSummary();
            summary.setHsnCode(dto.getHsnCode());
            summary.setTotalQty(dto.getTotalQty());
            summary.setTaxableValue(dto.getTaxableValue());
            summary.setGstAmount(dto.getGstAmount());
            summary.setMonth(monthYear);
            hsnSummaryRepository.save(summary);
        }
        System.out.println("Generated HSN Summary for " + monthYear);
    }

    public List<HsnSummary> getSummaries(String month) {
        if (month == null || month.isEmpty()) {
            return hsnSummaryRepository.findAll();
        }
        return hsnSummaryRepository.findByMonth(month);
    }
}

