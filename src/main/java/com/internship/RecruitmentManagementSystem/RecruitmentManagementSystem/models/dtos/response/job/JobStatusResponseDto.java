package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobStatusResponseDto {
    private Integer jobId;
    private JobStatus status;
    private int totalRows;
    private int successRows;
    private int failedRows;
    private List<BulkUploadRowResponseDto> rowDetails;
}
