package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.BulkJobResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.JobStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BulkUploadJobServiceInterface {
    Integer processBulkUploadJob(MultipartFile file,Integer uploadedById);
    JobStatusResponseDto getJobStatus(Integer jobId);
    List<BulkJobResponseDto> getAllBulkEntries();
}
