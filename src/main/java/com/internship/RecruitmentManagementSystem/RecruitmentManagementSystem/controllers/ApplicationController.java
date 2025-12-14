package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other.ThreshHoldScore;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<?> createApplication(@RequestBody @Valid ApplicationCreateDto newApplication) {
        log.info("Creating new application for candidateId: {} and positionId: {}",
                newApplication.getCandidateId(), newApplication.getPositionId());
        var createdApplication = applicationService.addApplication(newApplication);
        log.info("Application created with applicationId: {}", createdApplication.getApplicationId());
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    @PatchMapping("/{applicationId}/application-status/{applicationStatusId}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Integer applicationId,
            @PathVariable Integer applicationStatusId,
            @RequestBody @Valid ApplicationStatusUpdateDto applicationStatus
            ){
        return new ResponseEntity<>(applicationService.updateApplicationStatus(applicationId,applicationStatusId,applicationStatus),
                HttpStatus.OK);
    }

    @GetMapping("/mapped/position/{positionId}")
    public ResponseEntity<?> getMatchedApplications(
            @PathVariable Integer positionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching matched applications for positionId : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        ResponseEntity<?> response = new ResponseEntity<>(
                applicationService.getMatchedApplications(positionId,page, size, sortBy, sortDir),
                HttpStatus.OK);
        log.info("Fetched matched applications for positionId : {}",positionId);
        return response;
    }

    @PutMapping("/mapped/position/{positionId}")
    public ResponseEntity<?> matchApplicationsForPosition(@PathVariable Integer positionId,
                                                          @RequestBody ThreshHoldScore thresholdScore) {
        log.info("Matching applications for positionId: {}", positionId);
        var res = applicationService.matchApplicationsForPosition(positionId,thresholdScore.getThresholdScore());
        log.info("Matched applications for positionId: {}", positionId);
        return new ResponseEntity<>("Total "+ res +" Applications matched for positionId : " + positionId,
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching all applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        ResponseEntity<?> response = new ResponseEntity<>(
                applicationService.getAllApplications(page, size, sortBy, sortDir),
                HttpStatus.OK);
        log.info("Fetched applications successfully");
        return response;
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasAnyRole('RECRUITER')")
    public ResponseEntity<?> getAllApplicationsByRecruiter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer recruiterId = currentUser.getUserId();
        log.info("Fetching all applications By Recruiter- page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        ResponseEntity<?> response = new ResponseEntity<>(
                applicationService.getAllApplicationsByRecruiter(recruiterId,page,size,sortBy,sortDir),
                HttpStatus.OK);
        log.info("Fetched applications By Recruiter successfully ");
        return response;
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getCandidateApplications(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
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
            @RequestParam(defaultValue = "desc") String sortDir) {
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
        var application = applicationService.getApplication(applicationId);
        log.info("Fetched application for applicationId: {}", applicationId);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @GetMapping("/shortlists")
    public ResponseEntity<?> getShortlistedApplication(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching Shortlisted applications");
        var application = applicationService.getAllShortlistedApplications(page,size,sortBy,sortDir);
        log.info("Fetched Shortlisted applications");
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @GetMapping("/shortlists/recruiter")
    @PreAuthorize("hasAnyRole('RECRUITER')")
    public ResponseEntity<?> getShortlistedApplicationByRecruiter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer recruiterId = currentUser.getUserId();
        log.info("Fetching Shortlisted applications by Recruiter : {}",recruiterId);
        var application = applicationService.getAllShortlistedApplicationsByRecruiter(recruiterId,page,size,sortBy,sortDir);
        log.info("Fetched Shortlisted applications by Recruiter : {} ",recruiterId);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @GetMapping("/shortlists/reviewer")
    @PreAuthorize("hasAnyRole('REVIEWER')")
    public ResponseEntity<?> getShortlistedApplicationByReviewer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer reviewerId = currentUser.getUserId();
        log.info("Fetching Shortlisted applications by Reviewer : {}",reviewerId);
        var application = applicationService.getAllShortlistedApplicationsByReviewer(reviewerId,page,size,sortBy,sortDir);
        log.info("Fetched Shortlisted applications by Reviewer : {} ",reviewerId);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @GetMapping("/shortlists/candidate/{candidateId}")
    public ResponseEntity<?> getCandidateShortlistedApplication(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching Shortlisted application with candidateId : {}",candidateId);
        var application = applicationService.getCandidateShortlistedApplications(candidateId,page,size,sortBy,sortDir);
        log.info("Fetched Shortlisted applications with candidateId : {}",candidateId);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @GetMapping("/shortlists/position/{positionId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    public ResponseEntity<?> getPositionShortlistedApplication(
            @PathVariable Integer positionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching Shortlisted application with positionId : {}",positionId);
        var application = applicationService.getPositionShortlistedApplications(positionId,page,size,sortBy,sortDir);
        log.info("Fetched Shortlisted applications with positionId : {}",positionId);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @GetMapping("/shortlists/position/{positionId}/reviewer")
    @PreAuthorize("hasAnyRole('REVIEWER')")
    public ResponseEntity<?> getPositionShortlistedApplicationByReviewer(
            @PathVariable Integer positionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer reviewerId = currentUser.getUserId();
        log.info("Fetching Shortlisted application By Reviewer with positionId : {}",positionId);
        var application = applicationService.getPositionShortlistedApplicationsByReviewer(positionId,reviewerId,page,size,sortBy,sortDir);
        log.info("Fetched Shortlisted applications By Reviewer with positionId : {}",positionId);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @PatchMapping("/{applicationId}/shortlist")
    public ResponseEntity<?> shortlistApplication(@PathVariable Integer applicationId) {
        log.info("Shortlisting application with applicationId: {}", applicationId);
        applicationService.shortlistApplication(applicationId);
        log.info("Shortlisted application for applicationId: {}", applicationId);
        return new ResponseEntity<>("Application With applicationId : "+applicationId+" Shortlisted Successfully !",
                HttpStatus.OK);
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
