package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.BulkJobResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.JobStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BulkUploadJobServiceInterface {
    Integer processBulkUploadJob(MultipartFile file,Integer uploadedById);
    JobStatusResponseDto getJobStatus(Integer jobId);
    PaginatedResponse<BulkJobResponseDto> getAllBulkEntries(int page, int size, String sortBy, String sortDir);
}
