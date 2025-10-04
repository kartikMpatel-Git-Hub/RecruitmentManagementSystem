package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDto {

    private String userName;
    private String userEmail;
    private String userPassword;
    private String userImageUrl;

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
