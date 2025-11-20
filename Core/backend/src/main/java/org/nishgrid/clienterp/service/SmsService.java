package org.nishgrid.clienterp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.model.SmsNotification;
import org.nishgrid.clienterp.repository.SmsNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SmsService {
    @Autowired private SmsNotificationRepository smsLogRepository;

    @Value("${twilio.account.sid}")
    private String accountSid;
    @Value("${twilio.auth.token}")
    private String authToken;
    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Async
    public void sendInvoiceSms(SalesInvoice invoice) {
        SmsNotification log = new SmsNotification();
        log.setCustomer(invoice.getCustomer());
        log.setMessageType("Invoice");
        log.setStatus("PENDING");
        log.setTimestamp(LocalDateTime.now());
        smsLogRepository.save(log);

        try {
            Twilio.init(accountSid, authToken);
            String messageBody = String.format(
                    "Thank you, %s! Your invoice %s for amount %.2f has been generated.",
                    invoice.getCustomer().getName(),
                    invoice.getInvoiceNo(),
                    invoice.getNetAmount()
            );

            Message.creator(
                    new PhoneNumber(invoice.getCustomer().getMobile()), // Customer's phone number
                    new PhoneNumber(twilioPhoneNumber), // Your Twilio phone number
                    messageBody
            ).create();

            log.setStatus("SENT");
        } catch (Exception e) {
            log.setStatus("FAILED");
            // You might want to log the specific error message from the exception `e`
        }
        smsLogRepository.save(log);
    }
}