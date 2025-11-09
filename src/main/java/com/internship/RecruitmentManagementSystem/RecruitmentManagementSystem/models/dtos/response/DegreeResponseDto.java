package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Stream;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DegreeResponseDto {
    private Integer degreeId;

    private String degree;

    private Stream stream;

}
