package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ApplicationStatusDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<?> createApplication(@RequestBody @Valid ApplicationDto newApplication) {
        log.info("Creating new application for candidateId: {} and positionId: {}",
                newApplication.getCandidateId(), newApplication.getPositionId());
        ApplicationDto createdApplication = applicationService.addApplication(newApplication);
        log.info("Application created with applicationId: {}", createdApplication.getApplicationId());
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    @PutMapping("/{applicationId}")
    public ResponseEntity<?> updateApplication(
            @PathVariable Integer applicationId,
            @RequestBody @Valid ApplicationDto newApplication) {
        log.info("Updating application with applicationId: {}", applicationId);
        ApplicationDto updatedApplication = applicationService.updateApplication(applicationId, newApplication);
        log.info("Application updated with applicationId: {}", updatedApplication.getApplicationId());
        return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
    }

    @PatchMapping("/{applicationId}/application-status/{applicationStatusId}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Integer applicationId,
            @PathVariable Integer applicationStatusId,
            @RequestBody @Valid ApplicationStatusDto applicationStatus
            ){
        return new ResponseEntity<>(applicationService.updateApplicationStatus(applicationId,applicationStatusId,applicationStatus),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Fetching all applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        ResponseEntity<?> response = new ResponseEntity<>(
                applicationService.getAllApplications(page, size, sortBy, sortDir),
                HttpStatus.OK);
        log.info("Fetched applications successfully");
        return response;
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getCandidateApplications(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Fetching all applications With candidateId : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",candidateId, page, size, sortBy, sortDir);
        ResponseEntity<?> response = new ResponseEntity<>(
                applicationService.getCandidateApplications(candidateId,page, size, sortBy, sortDir),
                HttpStatus.OK);
        log.info("Fetched applications With candidateId : {}",candidateId);
        return response;
    }

    @GetMapping("/candidate/{candidateId}/id")
    public ResponseEntity<?> getCandidateApplicationsId(
            @PathVariable Integer candidateId
    ) {
        log.info("Fetching all applicationId With candidateId : {}",candidateId);
        ResponseEntity<?> response = new ResponseEntity<>(
                applicationService.getCandidateApplicationId(candidateId),
                HttpStatus.OK);
        log.info("Fetched applicationId With candidateId : {}",candidateId);
        return response;
    }

    @GetMapping("/position/{positionId}")
    public ResponseEntity<?> getPositionApplications(
            @PathVariable Integer positionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Fetching all applications With positionId : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        ResponseEntity<?> response = new ResponseEntity<>(
                applicationService.getPositionApplications(positionId,page, size, sortBy, sortDir),
                HttpStatus.OK);
        log.info("Fetched applications With positionId : {}",positionId);
        return response;
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplication(@PathVariable Integer applicationId) {
        log.info("Fetching application with applicationId: {}", applicationId);
        ApplicationDto application = applicationService.getApplication(applicationId);
        log.info("Fetched application for applicationId: {}", applicationId);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable Integer applicationId) {
        log.info("Deleting application with applicationId: {}", applicationId);
        applicationService.deleteApplication(applicationId);
        log.info("Deleted application with applicationId: {}", applicationId);
        return new ResponseEntity<>("Application With applicationId : " + applicationId + " Deleted !",
                HttpStatus.OK);
    }
}
