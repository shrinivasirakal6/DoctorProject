package com.DAA.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SmsService smsService; // Twilio wrapper

    public OtpService(RedisTemplate<String, Object> redisTemplate, SmsService smsService) {
        this.redisTemplate = redisTemplate;
        this.smsService = smsService;
    }

    // Generate and store OTP
    public void generateOtp(String phone) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        String key = "OTP:doctor:" +"+"+ phone;
        phone = "+"+phone;

        // Store OTP with 5 min expiry
        redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);

        // Send OTP via SMS (Twilio)
        smsService.sendSms(phone, "Your OTP is: " + otp);
    }

    // Validate OTP
    public boolean validateOtp(String phone, String inputOtp) {
        phone="+"+phone;
        String key = "OTP:doctor:" + phone;
        Object storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            redisTemplate.delete(key); // prevent reuse
            return true;
        }
        return false;
    }
}

