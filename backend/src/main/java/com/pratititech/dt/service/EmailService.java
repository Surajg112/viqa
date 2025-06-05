package com.pratititech.dt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp, String firstName, String lastName) throws MessagingException {
        String fullName = firstName + " " + lastName;
        String subject = "Verify Your E-Mail Address";

        String htmlContent = "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: Arial, sans-serif; background: #f5f8fa; padding: 20px; }" +
                ".container { background: white; max-width: 600px; margin: auto; border-radius: 10px; padding: 20px; }" +
                ".header { text-align: center; background-color: #2859e2; color: white; padding: 20px; border-radius: 10px 10px 0 0; }" +
                ".otp-box { display: inline-block; border: 2px solid #2859e2; padding: 10px 20px; font-size: 24px; font-weight: bold; border-radius: 5px; }" +
                ".button { background-color: #f26522; color: white; padding: 10px 20px; border: none; border-radius: 5px; text-decoration: none; font-weight: bold; display: inline-block; }" +
                ".footer { margin-top: 30px; color: #555; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h2>THANKS FOR SIGNING UP!</h2><h3>Verify Your E-Mail Address</h3></div>" +
                "<p>Hello " + fullName + ",</p>" +
                "<p>Please use the following One Time Password (OTP)</p>" +
                "<div>";

        for (char digit : otp.toCharArray()) {
            htmlContent += "<span class='otp-box'>" + digit + "</span>";
        }

        htmlContent += "</div>" +
                "<p>This passcode will only be valid for the next <strong>5 minutes</strong>.</p>" +
                "<div class='footer'><p>Thank you,<br/>PratitiTech Team</p></div>" +
                "</div></body></html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = isHtml

        mailSender.send(message);
    }
}
