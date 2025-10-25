package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DegreeModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UniversityModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateEducationDto {

    Integer candidateEducationId;
    Integer candidate;
    String candidateName;
    Integer university;
    String universityName;
    Integer degree;
    String degreeName;
    Double percentage;
    Integer passingYear;

}
