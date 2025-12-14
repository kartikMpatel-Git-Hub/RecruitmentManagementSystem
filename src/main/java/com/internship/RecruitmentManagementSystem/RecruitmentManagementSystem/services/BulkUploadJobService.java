package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.BulkJobResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.BulkUploadRowResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.JobStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.JobStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadRowResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.BulkUploadJobRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.BulkUploadRowResultRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.AsyncServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.BulkUploadJobServiceInterface;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkUploadJobService implements BulkUploadJobServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(BulkUploadJobService.class);

    private final BulkUploadJobRepository bulkUploadJobRepository;
    private final BulkUploadRowResultRepository bulkUploadRowResultRepository;
    private final AsyncServiceInterface asyncService;
    private final UserRepository userRepository;

    @Override
    public Integer processBulkUploadJob(MultipartFile file, Integer uploadedById) {

        logger.info("Starting bulk upload process. UploadedBy ID: {}", uploadedById);

        try {
            logger.debug("Fetching user who uploaded the file. User ID: {}", uploadedById);
            UserModel uploadedBy = userRepository.findById(uploadedById).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", uploadedById.toString())
            );

            logger.debug("Creating BulkUploadJob entity for file: {}", file.getOriginalFilename());

            BulkUploadJob job = new BulkUploadJob();
            job.setFileName(file.getOriginalFilename());
            job.setStatus(JobStatus.PENDING);
            job.setStartedAt(LocalDateTime.now());
            job.setUploadedBy(uploadedBy);

            bulkUploadJobRepository.save(job);
            logger.info("Bulk upload job created with Job ID: {}", job.getJobId());

            logger.info("Initiating asynchronous processing for Job ID: {}", job.getJobId());
            byte[] fileBytes = file.getBytes();
            asyncService.processAsync(job, fileBytes,file.getOriginalFilename());

            return job.getJobId();

        } catch (Exception e) {
            throw new FailedProcessException("Failed to process bulk upload job: " + e.getMessage());
        }
    }

    @Override
    public JobStatusResponseDto getJobStatus(Integer jobId) {

        logger.info("Fetching status for BulkUpload Job ID: {}", jobId);

        BulkUploadJob job = bulkUploadJobRepository.findById(jobId).orElseThrow(
                () -> new ResourceNotFoundException("BulkUploadJob", "id", jobId.toString())
        );

        logger.debug("Job status retrieved: {}", job.getStatus());
        List<BulkUploadRowResult> rowResult = bulkUploadRowResultRepository.findByJob(job).stream().toList();

        JobStatusResponseDto res = new JobStatusResponseDto();
        res.setJobId(job.getJobId());
        res.setStatus(job.getStatus());
        res.setTotalRows(job.getTotalRows());
        res.setSuccessRows(job.getSuccessRows());
        res.setFailedRows(job.getFailedRows());
        res.setRowDetails(rowResult.stream().map(result ->{
            BulkUploadRowResponseDto rowDetails = new BulkUploadRowResponseDto();
            rowDetails.setRowNumber(result.getRowNum());
            rowDetails.setStatus(result.isSuccess() ? "ADDED" : "FAILED");
            rowDetails.setMessage(result.getErrorMessage());
            return rowDetails;
        }).toList());
        logger.info("Successfully fetched status for Job ID: {}", jobId);

        return res;
    }

    @Override
    public List<BulkJobResponseDto> getAllBulkEntries() {

        logger.info("Fetching all bulk upload entries");

        List<BulkUploadJob> jobs = bulkUploadJobRepository.findAll();
        logger.debug("Total bulk jobs found: {}", jobs.size());

        return jobs.isEmpty()
                ? List.of()
                : jobs.stream().map(this::convertor).toList();
    }

    private BulkJobResponseDto convertor(BulkUploadJob entity) {
        BulkJobResponseDto dto = new BulkJobResponseDto();
        dto.setJobId(entity.getJobId());
        dto.setFileName(entity.getFileName());
        dto.setStatus(entity.getStatus());
        dto.setTotalRows(entity.getTotalRows());
        dto.setSuccessRows(entity.getSuccessRows());
        dto.setFailedRows(entity.getFailedRows());
        dto.setStartedAt(entity.getStartedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setUploadedById(entity.getUploadedBy().getUserId());
        return dto;
    }

}
