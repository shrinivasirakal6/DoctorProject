package com.DAA.Config;


import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String trialNumber;

    public TwilioConfig() {}

    @Value("${twilio.account-sid}")
    public void initTwilio(String accountSid) {
        Twilio.init(accountSid, authToken);
    }
}

