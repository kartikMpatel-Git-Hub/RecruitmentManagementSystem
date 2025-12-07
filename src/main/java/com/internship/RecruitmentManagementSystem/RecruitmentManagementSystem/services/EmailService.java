package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.EmailServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceInterface {

    private final JavaMailSender mailSender;

    @Override
    @Async
    @Retryable(
            value = { MessagingException.class, MailSendException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendMail(String from, String to, String subject, String body) {

        try{
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mail);
            System.out.println("Email sent: " + to);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Recover
    public void recover(Exception e, String from, String to, String subject, String htmlBody) {
        System.out.println("Email failed permanently after retries: " + to);
    }
}
