package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.documentVerification.DocumentReviewRequestDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.documentVerification.DocumentResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.documentVerification.DocumentVerificationResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentVerificationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentVerificationServiceInterface {
    void uploadDocument(
            Integer candidateId,
            MultipartFile file
    ) throws InvalidImageFormateException, IOException;

    void reviewDocument(
            Integer applicationId,
            DocumentReviewRequestDto documentReviewRequestDto
    );
    void deleteDocument(Integer documentId);

    void finalizeVerification(Integer documentVerificationId);

    PaginatedResponse<DocumentVerificationResponseDto> getAllDocumentVerificationApplications(DocumentVerificationStatus status, Integer page, Integer size, String sortBy, String sortDir);

    DocumentVerificationResponseDto getDocumentVerificationApplicationByApplication(Integer applicationId);

    void editDocument(Integer documentId, MultipartFile file) throws InvalidImageFormateException, IOException;
}
