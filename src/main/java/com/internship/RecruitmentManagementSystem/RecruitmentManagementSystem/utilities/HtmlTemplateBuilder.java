package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class HtmlTemplateBuilder {
    public String loadTemplate(String fileName) {
        try {
            InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("templates/email/" + fileName);
            if (input != null) {
                return new String(input.readAllBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to load email template");
        }
        return "";
    }

    public String buildCandidateInterviewTemplate(String name,
                                         String date,
                                         String time,
                                         String interviewers,
                                         String role,
                                         String link) {

        String template = loadTemplate("interview-template.html");

        return template
                .replace("{{name}}", name)
                .replace("{{date}}", date)
                .replace("{{time}}", time)
                .replace("{{interviewers}}", interviewers)
                .replace("{{role}}", role)
                .replace("{{link}}", link);
    }

    public String buildInterviewerInterviewTemplate(String interviewerName,
                                           String candidateName,
                                           String date,
                                           String time,
                                           String role,
                                           String otherInterviewers,
                                           String link) {

        String template = loadTemplate("interviewer-template.html");

        return template
                .replace("{{interviewerName}}", interviewerName)
                .replace("{{candidateName}}", candidateName)
                .replace("{{date}}", date)
                .replace("{{time}}", time)
                .replace("{{role}}", role)
                .replace("{{otherInterviewers}}", otherInterviewers)
                .replace("{{link}}", link);
    }

    public String buildCandidateCredentialTemplate(String candidateName,
                                                   String username,
                                           String password) {

        String template = loadTemplate("candidate-credential-template.html");

        return template
                .replace("{{candidateName}}", candidateName)
                .replace("{{userName}}", username)
                .replace("{{password}}", password);
    }

}
