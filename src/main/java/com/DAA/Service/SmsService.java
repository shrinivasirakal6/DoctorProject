package com.DAA.Service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.phone-number}")
    private String fromNumber;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
        System.out.println("✅ Twilio initialized successfully with account SID: " + accountSid);
    }

    public void sendSms(String to, String body) {
        Message.creator(
                new PhoneNumber(to),     // Receiver’s number
                new PhoneNumber(fromNumber), // Twilio trial number
                body
        ).create();
    }
}

