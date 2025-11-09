package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.PositionRoundCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.PositionType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Deprecated
public class PositionDto {

    private Integer positionId;

    private String positionTitle;

    private String positionDescription;

    private String positionCriteria;

    private PositionType positionType;

    private Double positionSalary;

    private String positionLocation;

    private String positionLanguage;

    private Integer positionTotalOpening;

    private PositionStatusDto positionStatus;

    private List<PositionRequirementDto> positionRequirements;

    private Integer positionApplications;

    private List<DegreeDto> positionRequiredEducations = new ArrayList<>();

    private Integer createdById;

    private String createdByName;

    private List<PositionRoundCreateDto> positionRounds;
}
