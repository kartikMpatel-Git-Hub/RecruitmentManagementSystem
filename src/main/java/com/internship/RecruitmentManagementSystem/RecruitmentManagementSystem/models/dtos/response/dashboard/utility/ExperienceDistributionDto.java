package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

@Data
public class ExperienceDistributionDto {
    private long lessThanOneYear;
    private long oneToThreeYears;
    private long threeToFiveYears;
    private long moreThanFiveYears;
}
