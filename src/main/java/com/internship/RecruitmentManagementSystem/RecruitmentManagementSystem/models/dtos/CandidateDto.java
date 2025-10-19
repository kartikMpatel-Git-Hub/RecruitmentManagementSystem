package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDto {

    private String userName;
    private String userEmail;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPassword;
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

    private List<SkillDto> candidateSkills;
}

/*

    {
  "candidateFirstName": "John",
  "candidateMiddleName": "Michael",
  "candidateLastName": "Doe",
  "candidateGender": "Male",
  "candidateDateOfBirth": "1995-06-15",
  "candidateAddress": "123 Main Street, Apartment 4B",
  "candidateCity": "New York",
  "candidateState": "New York",
  "candidateCountry": "USA",
  "candidateZipCode": "10001",
  "candidatePhoneNumber": "9876543210",
  "candidateTotalExperienceInYears": 5
}



 */