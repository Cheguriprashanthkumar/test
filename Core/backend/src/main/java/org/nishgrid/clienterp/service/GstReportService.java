package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.Gstr3bAggregationDTO;
import org.nishgrid.clienterp.model.Gstr3bSummary;
import org.nishgrid.clienterp.repository.Gstr1ItemRepository;
import org.nishgrid.clienterp.repository.Gstr3bSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GstReportService {

    @Autowired private Gstr1ItemRepository gstr1ItemRepository;
    @Autowired private Gstr3bSummaryRepository gstr3bSummaryRepository;


    public Gstr3bSummary generateAndSaveGstr3bSummary(YearMonth yearMonth) {
        String monthYear = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        Gstr3bAggregationDTO summaryData = gstr1ItemRepository.getGstr3bSummaryForMonth(monthYear);

        Gstr3bSummary summary = new Gstr3bSummary();
        summary.setMonthYear(monthYear);
        summary.setOutwardTaxableSupplies(summaryData.getOutwardTaxableSupplies());
        summary.setCgst(summaryData.getCgst());
        summary.setSgst(summaryData.getSgst());
        summary.setIgst(summaryData.getIgst());
        summary.setInwardSupplies(0.0); // Assuming no purchase data yet
        summary.setFiledStatus(Gstr3bSummary.GstFiledStatus.PENDING);

        return gstr3bSummaryRepository.save(summary);
    }


    @Scheduled(cron = "0 0 1 1 * *")
    public void generateScheduledGstr3bSummary() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        generateAndSaveGstr3bSummary(lastMonth);
        System.out.println("Generated GSTR-3B Summary for " + lastMonth);
    }

    public List<Gstr3bSummary> getAllGstr3bSummaries() {
        return gstr3bSummaryRepository.findAll();
    }
}