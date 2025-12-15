package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.documentVerification;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentStatus;
import lombok.Data;

@Data
public class DocumentReviewRequestDto {
    DocumentStatus documentStatus;
    String rejectionReason;
}
