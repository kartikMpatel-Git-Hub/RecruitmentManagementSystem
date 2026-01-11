package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.education;

import lombok.Data;

@Data
public class CandidateEducationUpdateDto {
    Integer university;
    Integer degree;
    Double percentage;
    Integer passingYear;
}
