package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApplicationStatusCreateDto {

    private Integer applicationStatusId;

    private ApplicationStatus applicationStatus;

    private String applicationFeedback;

}
