package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job;

import lombok.Data;

@Data
public class BulkUploadRowResponseDto {
    private Integer rowNumber;
    private String status;
    private String message;
}
