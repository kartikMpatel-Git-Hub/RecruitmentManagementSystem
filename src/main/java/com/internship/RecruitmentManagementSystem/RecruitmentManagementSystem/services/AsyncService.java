package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.JobStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadRowResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.BulkUploadJobRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.BulkUploadRowResultRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.AsyncServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.BulkUploadCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AsyncService implements AsyncServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);
    private final BulkUploadJobRepository bulkUploadJobRepository;

    private final BulkUploadRowResultRepository bulkUploadRowResultRepository;
    private final BulkCandidateService candidateService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    public void processAsync(BulkUploadJob job, byte[] fileByte,String fileName) {

        logger.info("Async processing started for Job ID: {}", job.getJobId());

        job.setStatus(JobStatus.IN_PROGRESS);
        bulkUploadJobRepository.save(job);

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileByte))) {

            Sheet sheet = workbook.getSheetAt(0);
            int total = sheet.getPhysicalNumberOfRows() - 1;

            logger.debug("Total rows detected (excluding header): {}", total);

            job.setTotalRows(total);

            int success = 0;
            int failed = 0;

            for (int i = 1; i <= total; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    logger.warn("Empty row detected at Excel row index: {}", i + 1);
                    continue;
                }

                logger.debug("Processing row number: {}", i + 1);

                BulkUploadRowResult rowResult = new BulkUploadRowResult();
                rowResult.setJob(job);
                rowResult.setRowNum(i + 1);

                try {
                    candidateService.processSingleRow(row);
                    rowResult.setSuccess(true);
                    success++;

                    logger.debug("Row {} processed successfully", i + 1);

                } catch (Exception e) {
                    rowResult.setSuccess(false);
                    rowResult.setErrorMessage(e.getMessage());
                    failed++;

                    logger.error("Row {} failed: {}", i + 1, e.getMessage());
                }

                bulkUploadRowResultRepository.save(rowResult);
            }

            logger.info("Bulk Upload Completed for Job ID: {} | Success: {} | Failed: {}", job.getJobId(), success, failed);

            job.setSuccessRows(success);
            job.setFailedRows(failed);
            job.setStatus(JobStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());

            bulkUploadJobRepository.save(job);

            eventPublisher.publishEvent(
                    new BulkUploadCompletedEvent(job.getJobId())
            );

            logger.info("Bulk upload completed for Job ID: {}", job.getJobId());

        } catch (Exception e) {
            logger.error("Unexpected error while processing Job ID {}: {}", job.getJobId(), e.getMessage());

            job.setStatus(JobStatus.FAILED);
            bulkUploadJobRepository.save(job);
        }
    }

}
