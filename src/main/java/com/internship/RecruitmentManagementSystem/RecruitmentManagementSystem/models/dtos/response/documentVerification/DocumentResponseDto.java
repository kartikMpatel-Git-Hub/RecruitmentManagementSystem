package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.documentVerification;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentStatus;
import lombok.Data;

@Data
public class DocumentResponseDto {

    private Integer documentId;
    private String documentName;
    private String documentUrl;
    private DocumentStatus documentStatus;
    private String rejectionReason;
}
