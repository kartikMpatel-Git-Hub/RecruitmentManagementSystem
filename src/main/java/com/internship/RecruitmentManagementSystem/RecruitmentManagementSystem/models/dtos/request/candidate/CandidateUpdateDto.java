package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CandidateUpdateDto {
    private String candidateFirstName;
    private String candidateMiddleName;
    private String candidateLastName;
    private String candidateGender;
    private LocalDate candidateDateOfBirth;
    private String candidateAddress;
    private String candidateCity;
    private String candidateState;
    private String candidateCountry;
    private String candidateZipCode;
    private String candidatePhoneNumber;
    private String candidateResumeUrl;
    private Integer candidateTotalExperienceInYears;
}
