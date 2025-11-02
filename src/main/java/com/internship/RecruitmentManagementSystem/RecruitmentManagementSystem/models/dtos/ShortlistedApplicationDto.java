package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortlistedApplicationDto {

    private Integer shortlistedApplicationId;

    private ApplicationDto application;

    private ShortlistedApplicationStatusDto shortlistedApplicationStatus;

}
