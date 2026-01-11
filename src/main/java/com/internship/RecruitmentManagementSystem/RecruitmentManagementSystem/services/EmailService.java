package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other.CandidateRowData;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.EmailServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.HtmlTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
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
    private final HtmlTemplateBuilder templateBuilder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void mailToCandidate(CandidateRowData data) {
        try {
            String mailBody = templateBuilder.buildCandidateCredentialTemplate(
                    data.getFirstName() + " " + data.getLastName(),
                    data.getUserName(),
                    data.getUserPassword()
            );

            sendMail(
                    fromEmail,
                    data.getUserEmail(),
                    "Account Credentials - Recruitment Management System",
                    mailBody
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void mailToCredentialCandidate(String email, String userName, String password) {
        try {
            String mailBody = templateBuilder.buildCandidateCredentialTemplate(
                    email,
                    userName,
                    password
            );

            sendMail(
                    fromEmail,
                    email,
                    "Account Credentials - Recruitment Management System",
                    mailBody
            );


        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void mailToCandidate(String candidateName,
                                @NotEmpty(message = "Email Can't Be Empty !")
                                @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                                String candidateEmail,
                                String interviewDate,
                                String interviewTime,
                                String interviewerList,
                                String jobRole,
                                String link) {
        String mailBody = templateBuilder.buildCandidateInterviewTemplate(
                candidateName,
                interviewDate,
                interviewTime,
                interviewerList,
                jobRole,
                link
        );

        sendMail(
                fromEmail,
                candidateEmail,
                "Your Interview is Scheduled",
                mailBody
        );
    }

    @Override
    public void mailToInterviewer(String username,
                                  @NotEmpty(message = "Email Can't Be Empty !")
                                  @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                                  String userEmail,
                                  String candidateName,
                                  String interviewDate,
                                  String interviewTime,
                                  String jobRole,
                                  String link,
                                  String interviewerList) {
        String mailBody = templateBuilder.buildInterviewerInterviewTemplate(
                username,
                candidateName,
                interviewDate,
                interviewTime,
                interviewerList,
                jobRole,
                link
        );

        sendMail(
                fromEmail,
                userEmail,
                "Upcoming Interview Assigned",
                mailBody
        );
    }

    @Override
    public void mailToCandidate(String candidateName,
                                @NotEmpty(message = "Email Can't Be Empty !")
                                @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                                String candidateEmail,
                                String jobRole
    ) {
        String requireDocument = "<li>Aadhaar Card</li>" +
                "<li>PAN Card</li>" +
                "<li>Educational Certificates</li>" +
                "<li>Experience Letter (if applicable)</li>";
        String mailBody = templateBuilder.buildCandidateDocumentVerificationTemplate(candidateName,jobRole,requireDocument);

        sendMail(
                fromEmail,
                candidateEmail,
                "Document Verification Required",
                mailBody
        );
    }

    @Override
    public void hireMailToCandidate(String candidateName,
                                    @NotEmpty(message = "Email Can't Be Empty !")
                                    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                                    String candidateEmail,
                                    String jobRole,
                                    String jobLocation
    ) {
        String mailBody = templateBuilder.buildCandidateHiredTemplate(candidateName,jobRole,jobLocation);

        sendMail(
                fromEmail,
                candidateEmail,
                "Congratulations! You’re Hired",
                mailBody
        );
    }

    @Override
    public void documentRejectMailToCandidate(String candidateName,
                                              @NotEmpty(message = "Email Can't Be Empty !")
                                              @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                                              String candidateEmail,
                                              String jobRole,
                                              String rejectReason
    ) {
        String mailBody = templateBuilder.buildDocumentRejectMail(
                "Test Company Pvt.Ltd",
                candidateName,
                jobRole,
                rejectReason,
                "Login To System !"
        );

        sendMail(
                fromEmail,
                candidateEmail,
                "Document Submission Update",
                mailBody
        );
    }

    @Async
    @Retryable(
            value = { MessagingException.class, MailSendException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    private void sendMail(String from, String to, String subject, String body) {

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
