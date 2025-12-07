package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.JobStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.BulkUploadJobServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.BulkUploadJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bulk-entries")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BulkEntryController {

    private final BulkUploadJobServiceInterface bulkUploadJobService;

    @PostMapping("/excel/upload/{uploadedById}")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @PathVariable("uploadedById")Integer uploadedById) {
        Integer jobId = bulkUploadJobService.processBulkUploadJob(file, uploadedById);
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobStatusResponseDto> status(@PathVariable Integer jobId) {
        var res = bulkUploadJobService.getJobStatus(jobId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBulkEntries() {
        var res = bulkUploadJobService.getAllBulkEntries();
        return ResponseEntity.ok(res);
    }

}
