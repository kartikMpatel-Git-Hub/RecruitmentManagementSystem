package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.CandidateUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/candidates")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;

    @PostMapping("/resume-entry")
    public ResponseEntity<?> addCandidate(
            @RequestParam("resume") MultipartFile resume
    ) {
        logger.info("Request for adding candidate based on resume received");
        CandidateResponseDto candidate = candidateService.processResume(resume);

        logger.info("Candidate added with candidateId : {}",candidate.getCandidateId());
        return new ResponseEntity<>(candidate, HttpStatus.OK);
    }

    @PutMapping(value = "/{candidateId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CandidateResponseDto> updateCandidate(
            @PathVariable Integer candidateId,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart("candidate") @Valid CandidateUpdateDto existingCandidate
    ) {
        logger.info("Request received to update candidate with ID: {}", candidateId);
        logger.debug("Calling CandidateService to update candidate with ID: {}", candidateId);
        CandidateResponseDto updatedCandidate = candidateService.updateCandidate(resume,existingCandidate, candidateId);
        logger.info("Successfully updated candidate with ID: {}", candidateId);

        return new ResponseEntity<>(updatedCandidate, HttpStatus.OK);
    }

    @PatchMapping(value = "/{candidateId}")
    public ResponseEntity<?> updateSkills(@PathVariable Integer candidateId,
                                          @RequestBody List<Integer> skillIds) {
        logger.info("Request received to update skills for candidate ID: {}", candidateId);
        logger.debug("Skill IDs to update: {}", skillIds);

        CandidateResponseDto candidateWithUpdatedSkills = candidateService.updateCandidateSkills(candidateId, skillIds);

        logger.info("Successfully updated skills for candidate ID: {}", candidateId);
        return new ResponseEntity<>(candidateWithUpdatedSkills, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{candidateId}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Integer candidateId) {
        logger.info("Request received to delete candidate with ID: {}", candidateId);

        Boolean isDeleted = candidateService.deleteCandidate(candidateId);
        if (isDeleted) {
            logger.info("Candidate deleted successfully: {}", candidateId);
            return new ResponseEntity<>(new ApiResponse(200, "Delete Successfully!", true), HttpStatus.OK);
        } else {
            logger.error("Error while deleting candidate with ID: {}", candidateId);
            return new ResponseEntity<>(new ApiResponse(500, "Error While Deleting!", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countCandidates() {
        logger.info("Request received to count total candidates");
        Long count = candidateService.countCandidates();
        logger.debug("Total candidates count: {}", count);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<PaginatedResponse<CandidateResponseDto>> getCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "candidateId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching paginated list of candidates - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<CandidateResponseDto> response = candidateService.getAllCandidates(page, size, sortBy, sortDir);
        logger.debug("Fetched {} candidates from page {}", response.getData().size(), page);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{candidateId}")
    public ResponseEntity<?> getCandidate(@PathVariable Integer candidateId) {
        logger.info("Fetching candidate details for ID: {}", candidateId);
        var candidate = candidateService.getCandidate(candidateId);
        logger.debug("Fetched candidate details: {}", candidate);
        return new ResponseEntity<>(candidate, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<?> getCandidateByUserId(@PathVariable Integer userId) {
        logger.info("Fetching candidate details for user ID: {}", userId);
        var candidate = candidateService.getCandidateByUserId(userId);
        logger.debug("Fetched candidate by user ID {}: {}", userId, candidate);
        return new ResponseEntity<>(candidate, HttpStatus.OK);
    }
}
