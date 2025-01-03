package com.airbnb.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    public void sendEmail(String toEmail,String subject,String message,String bookingUrl) {
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setTo(toEmail);
        sm.setSubject(subject);
        sm.setText(message);
        sm.setText(bookingUrl);
        MailSender javaMailSender= new JavaMailSenderImpl();
         javaMailSender.send(sm);
    }

}
