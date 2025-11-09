package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ShortlistedApplicationStaus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class ShortlistedApplicationStatusDto {
    private Integer shortlistedApplicationStatusId;

    private ShortlistedApplicationStaus shortlistedApplicationStatus;

    private String shortlistedApplicationFeedback;
}
