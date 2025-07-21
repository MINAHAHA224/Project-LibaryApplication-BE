package com.ThuVien.ThuVienAplication.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailerService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    public  boolean sendConfirmLink(String emailTo, String newPassword , String emailLogin) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirming link to user, email={}", emailTo);
        emailTo = "caothaiiop1234@gmail.com";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();


        Map<String, Object> properties = new HashMap<>();
        properties.put("emailLogin" , emailLogin);
        properties.put("newPassword", newPassword);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Phòng kĩ thuật");
        helper.setTo(emailTo);
        helper.setSubject("New account generate");
        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);
        log.info("emailLogin ={} , newPassword = {}" ,emailLogin, newPassword);
        try {
            mailSender.send(message);

            log.info("Confirming link has sent to user, email={}, linkConfirm={}", emailTo, newPassword);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error sending mail: {}", ex.getMessage(), ex);
            return false;
        }

    }



}
