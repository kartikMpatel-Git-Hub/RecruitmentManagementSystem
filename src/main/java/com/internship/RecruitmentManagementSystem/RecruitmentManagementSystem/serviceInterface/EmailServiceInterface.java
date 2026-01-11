package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other.CandidateRowData;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public interface EmailServiceInterface {
    void mailToCandidate(CandidateRowData data);

    void mailToCredentialCandidate(String email, String userName, String password);

    void mailToCandidate(String candidateName,
                         @NotEmpty(message = "Email Can't Be Empty !")
                         @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                         String candidateEmail,
                         String interviewDate,
                         String interviewTime,
                         String interviewerList,
                         String jobRole,
                         String link);

    void mailToInterviewer(String username,
                           @NotEmpty(message = "Email Can't Be Empty !")
                           @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                           String userEmail,
                           String candidateName,
                           String interviewDate,
                           String interviewTime,
                           String jobRole,
                           String link,
                           String interviewerList);

    void mailToCandidate(String candidateName,
                         @NotEmpty(message = "Email Can't Be Empty !")
                         @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                         String candidateEmail,
                         String jobRole
    );

    void hireMailToCandidate(String candidateName,
                             @NotEmpty(message = "Email Can't Be Empty !")
                             @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                             String candidateEmail,
                             String jobRole,
                             String jobLocation
    );

    void documentRejectMailToCandidate(String candidateName,
                                       @NotEmpty(message = "Email Can't Be Empty !")
                                       @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format!")
                                       String candidateEmail,
                                       String jobRole,
                                       String rejectReason
    );
}
