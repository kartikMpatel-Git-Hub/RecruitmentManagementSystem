package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.PositionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class PositionUpdateDto {
    private String positionTitle;

    private String positionDescription;

    private String positionCriteria;

    private PositionType positionType;

    private Double positionSalary;

    private String positionLocation;

    private String positionLanguage;

    private Integer positionTotalOpening;

    private PositionStatusUpdateDto positionStatus;

}
