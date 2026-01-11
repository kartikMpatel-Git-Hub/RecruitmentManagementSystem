package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class CandidateEducationDto {

//    Integer candidateEducationId;
    Integer candidate;
//    String candidateName;
    Integer university;
//    String universityName;
    Integer degree;
//    String degreeName;
    Double percentage;
    Integer passingYear;

}
