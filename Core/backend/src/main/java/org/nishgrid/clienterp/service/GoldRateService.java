package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.model.GoldRate;
import org.nishgrid.clienterp.repository.GoldRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class GoldRateService {

    @Autowired
    private GoldRateRepository goldRateRepository;

    public GoldRate getLatestRate() {
        return goldRateRepository.findTopByOrderByDateDesc();
    }


    @Scheduled(fixedRate = 300000)
    public void addSimulatedDailyRate() {
        Random random = new Random();
        GoldRate newRate = new GoldRate();
        newRate.setDate(LocalDateTime.now());
        newRate.setRate24k(6100.0 + random.nextInt(100));
        newRate.setRate22k(5800.0 + random.nextInt(100));
        newRate.setRate18k(5100.0 + random.nextInt(100));
        newRate.setRate14k(4300.0 + random.nextInt(100));
        newRate.setRate12k(3900.0 + random.nextInt(100));
        newRate.setRate10k(3500.0 + random.nextInt(100));
        newRate.setRate09k(3100.0 + random.nextInt(100));
        newRate.setFineSilver(750.0 + random.nextInt(20));
        newRate.setSterlingSilver(680.0 + random.nextInt(20));
        newRate.setCoinSilver(610.0 + random.nextInt(20));

        goldRateRepository.save(newRate);
        System.out.println("Simulated gold rates updated at: " + newRate.getDate());
    }
}