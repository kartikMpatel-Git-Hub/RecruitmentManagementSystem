package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class ShortlistedApplicationDto {

    private Integer shortlistedApplicationId;

    private ApplicationDto application;

    private ShortlistedApplicationStatusDto shortlistedApplicationStatus;

    private List<RoundDto> rounds;
}
