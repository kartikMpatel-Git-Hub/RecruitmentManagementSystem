package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.position;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeGetDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.PositionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class PositionCreateDto {
    private String positionTitle;

    private String positionDescription;

    private String positionCriteria;

    private PositionType positionType;

    private Double positionSalary;

    private String positionLocation;

    private String positionLanguage;

    private Integer positionTotalOpening;

    private Integer positionMinYearsOfExperience;

    private PositionStatusGetDto positionStatus;

    private List<PositionRequirementCreateDto> positionRequirements;

    private List<DegreeGetDto> positionRequiredEducations;

    private List<PositionRoundCreateDto> positionRounds;
}
