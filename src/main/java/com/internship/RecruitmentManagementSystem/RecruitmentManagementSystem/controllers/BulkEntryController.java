package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.JobStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.BulkUploadJobServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bulk-entries")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BulkEntryController {

    private static final Logger logger = LoggerFactory.getLogger(BulkEntryController.class);

    private final BulkUploadJobServiceInterface bulkUploadJobService;

    @PostMapping("/excel/upload/{uploadedById}")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @PathVariable("uploadedById") Integer uploadedById) {

        logger.info("Request received to upload bulk entries Excel file by user: {}", uploadedById);

        if (file == null || file.isEmpty()) {
            logger.error("No file provided for bulk upload by user: {}", uploadedById);
            return ResponseEntity.badRequest().body("File is required");
        }

        logger.debug("File received: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());
        logger.debug("Calling BulkUploadJobService to process file for user: {}", uploadedById);

        Integer jobId = bulkUploadJobService.processBulkUploadJob(file, uploadedById);

        logger.info("Bulk upload job created successfully with Job ID: {}", jobId);

        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobStatusResponseDto> status(@PathVariable Integer jobId) {

        logger.info("Request received to fetch status for Job ID: {}", jobId);
        JobStatusResponseDto res = bulkUploadJobService.getJobStatus(jobId);

        logger.debug("Job status for ID {}: {}", jobId, res);
        logger.info("Successfully fetched bulk upload status for Job ID: {}", jobId);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllBulkEntries() {

        logger.info("Request received to fetch all bulk upload entries");

        var res = bulkUploadJobService.getAllBulkEntries();
        logger.debug("Total bulk upload jobs fetched: {}", res.size());

        logger.info("Successfully fetched all bulk upload entries");
        return ResponseEntity.ok(res);
    }
}
