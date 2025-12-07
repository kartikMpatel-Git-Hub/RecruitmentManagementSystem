package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

public interface EmailServiceInterface {
    void sendMail(String from,String to,String subject,String body);
}
