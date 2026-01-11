package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class CandidateResponseDto {
    private String userName;
    private String userEmail;

    private String userImageUrl;

    private Integer candidateId;
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

    private Set<CandidateSkillResponseDto> candidateSkills;

    private Set<CandidateEducationResponseDto> candidateEducations;
}
