package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @PutMapping(value = "/{candidateId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CandidateDto> updateCandidate(
            @PathVariable Integer candidateId,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart("candidate") @Valid CandidateDto existingCandidate
    ) {
        logger.info("Request received to update candidate with ID: {}", candidateId);

        if (resume != null && !resume.isEmpty()) {
            logger.debug("Resume file provided for candidate ID: {}", candidateId);
            saveFiles(resume, existingCandidate);
        } else {
            logger.debug("No resume file provided for candidate ID: {}", candidateId);
        }

        logger.debug("Calling CandidateService to update candidate with ID: {}", candidateId);
        CandidateDto updatedCandidate = candidateService.updateCandidate(existingCandidate, candidateId);
        logger.info("Successfully updated candidate with ID: {}", candidateId);

        return new ResponseEntity<>(updatedCandidate, HttpStatus.OK);
    }

    @PatchMapping(value = "/{candidateId}")
    public ResponseEntity<?> updateSkills(@PathVariable Integer candidateId,
                                          @RequestBody List<Integer> skillIds) {
        logger.info("Request received to update skills for candidate ID: {}", candidateId);
        logger.debug("Skill IDs to update: {}", skillIds);

        CandidateDto candidateWithUpdatedSkills = candidateService.updateCandidateSkills(candidateId, skillIds);

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
    public ResponseEntity<PaginatedResponse<CandidateDto>> getCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "candidateId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching paginated list of candidates - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<CandidateDto> response = candidateService.getAllCandidates(page, size, sortBy, sortDir);
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
        CandidateDto candidate = candidateService.getCandidateByUserId(userId);
        logger.debug("Fetched candidate by user ID {}: {}", userId, candidate);
        return new ResponseEntity<>(candidate, HttpStatus.OK);
    }

    private void saveFiles(MultipartFile resume, CandidateDto candidateRegistrationDto) {
        logger.debug("Attempting to save resume for candidate: {}", candidateRegistrationDto.getCandidateFirstName());
        String resumeUrl = saveFile(resume);
        if (resumeUrl == null) {
            logger.error("Failed to upload resume for candidate: {}", candidateRegistrationDto.getCandidateFirstName());
            throw new FailedProcessException("Failed to upload resume");
        }
        logger.debug("Resume uploaded successfully: {}", resumeUrl);
        candidateRegistrationDto.setCandidateResumeUrl(resumeUrl);
    }

    private String saveFile(MultipartFile file) {
        try {
            logger.debug("Uploading file: {}", file.getOriginalFilename());

//            String fileUrl = imageKitService.uploadFile(file);
            String fileUrl = fileService.uploadImage(path,file);

            logger.debug("File uploaded successfully: {}", fileUrl);
            return fileUrl;
        }  catch (Exception e) {
            logger.error("Unexpected error while uploading file {}: {}", file.getOriginalFilename(), e.getMessage());
            return null;
        }
        catch (InvalidImageFormateException e) {
            throw new RuntimeException(e);
        }
    }
}
