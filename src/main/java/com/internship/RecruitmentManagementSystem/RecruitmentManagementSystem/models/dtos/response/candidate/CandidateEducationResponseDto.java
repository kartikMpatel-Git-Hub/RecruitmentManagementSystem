package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.degree.DegreeResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.university.UniversityResponseDto;
import lombok.Data;

@Data
public class CandidateEducationResponseDto {
    Integer candidateEducationId;

    UniversityResponseDto university;

    DegreeResponseDto degree;

    Double percentage;
    Integer passingYear;
}
