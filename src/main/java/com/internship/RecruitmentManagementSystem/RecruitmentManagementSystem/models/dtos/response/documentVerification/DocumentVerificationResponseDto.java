package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.documentVerification;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentVerificationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentVerificationResponseDto {

    private Integer documentVerificationId;
    private Integer applicationId;
    private Integer candidateId;
    private DocumentVerificationStatus verificationStatus;
    private String hrRemarks;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private List<DocumentResponseDto> documents;
}
