package com.airbnb.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class SmsService {
           @Value("${twilio.accountSid}")
        private String accountSid;

        @Value("${twilio.authToken}")
        private String authToken;

        @Value("${twilio.phoneNumber}")
        private String twilioPhoneNumber;

        public void sendSms(String toPhoneNumber,String messageBody) {
            Twilio.init(accountSid, authToken);
            Message twilioMessage = Message.creator(
                    new com.twilio.type.PhoneNumber(toPhoneNumber),
                    new com.twilio.type.PhoneNumber(twilioPhoneNumber), messageBody).create();
            System.out.println("Message SID: " + twilioMessage.getSid());
        }
    }




