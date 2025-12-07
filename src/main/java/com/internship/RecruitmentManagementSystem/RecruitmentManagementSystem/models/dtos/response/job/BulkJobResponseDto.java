package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.JobStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BulkJobResponseDto {
    private Integer jobId;

    private String fileName;

    private JobStatus status;

    private int totalRows;
    private int successRows;
    private int failedRows;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private Integer uploadedById;
}
