package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ShortlistedApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ShortlistedApplicationStatusDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.ShortlistedApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/shortlisted-applications")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class ShortlistedApplicationController {

//    private final ShortlistedApplicationService shortlistedApplicationService;
//
//    @PostMapping
//    public ResponseEntity<?> shortlistApplication(@RequestBody @Valid ShortlistedApplicationDto shortlistedApplication) {
//        log.info("Received request to shortlist applicationId={}",
//                shortlistedApplication.getApplication().getCandidateId());
//        var response = shortlistedApplicationService.shortListApplication(shortlistedApplication);
//        log.info("Shortlisted application created successfully with ID={}", response.getShortlistedApplicationId());
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    @PatchMapping("/{shortlistedApplicationId}/shortlisted-application-status/{shortlistedApplicationStatusId}")
//    public ResponseEntity<?> updateShortlistedApplicationStatus(
//            @PathVariable Integer shortlistedApplicationId,
//            @PathVariable Integer shortlistedApplicationStatusId,
//            @RequestBody @Valid ShortlistedApplicationStatusDto shortlistedApplicationStatus) {
//        log.info("Updating shortlisted application status: shortlistedApplicationId={}, statusId={}, newStatus={}",
//                shortlistedApplicationId, shortlistedApplicationStatusId, shortlistedApplicationStatus.getShortlistedApplicationStatus());
//        var response = shortlistedApplicationService.updateShortlistedApplicationStatus(shortlistedApplicationStatusId, shortlistedApplicationStatus);
//        log.info("Shortlisted application status updated successfully for shortlistedApplicationId={}", shortlistedApplicationId);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping
//    public ResponseEntity<?> getAllShortlistedApplications(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "30") int size,
//            @RequestParam(defaultValue = "shortlistedApplicationId") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        log.info("Fetching all shortlisted applications: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
//        var response = shortlistedApplicationService.getAllShortlistedApplications(page, size, sortBy, sortDir);
//        log.info("Fetched {} shortlisted applications", response.getData().size());
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/{shortlistedApplicationId}")
//    public ResponseEntity<?> getShortlistedApplication(@PathVariable Integer shortlistedApplicationId) {
//        log.info("Fetching shortlisted application with ID={}", shortlistedApplicationId);
//        var response = shortlistedApplicationService.getShortlistedApplication(shortlistedApplicationId);
//        log.info("Fetched shortlisted application: {}", response);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/candidate/{candidateId}")
//    public ResponseEntity<?> getCandidateShortlistedApplications(
//            @PathVariable Integer candidateId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "30") int size,
//            @RequestParam(defaultValue = "shortlistedApplicationId") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        log.info("Fetching shortlisted applications for candidateId={}, page={}, size={}", candidateId, page, size);
//        var response = shortlistedApplicationService.getCandidateShortlistedApplications(candidateId, page, size, sortBy, sortDir);
//        log.info("Fetched {} shortlisted applications for candidateId={}", response.getData().size(), candidateId);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/position/{positionId}")
//    public ResponseEntity<?> getPositionShortlistedApplications(
//            @PathVariable Integer positionId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "30") int size,
//            @RequestParam(defaultValue = "shortlistedApplicationId") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        log.info("Fetching shortlisted applications for positionId={}, page={}, size={}", positionId, page, size);
//        var response = shortlistedApplicationService.getPositionShortlistedApplications(positionId, page, size, sortBy, sortDir);
//        log.info("Fetched {} shortlisted applications for positionId={}", response.getData().size(), positionId);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
