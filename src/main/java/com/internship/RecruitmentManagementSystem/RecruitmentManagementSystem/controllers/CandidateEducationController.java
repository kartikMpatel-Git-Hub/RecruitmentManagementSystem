package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateEducationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateEducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate-educations")
@RequiredArgsConstructor
public class CandidateEducationController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateEducationController.class);

    private final CandidateEducationService candidateEducationService;

    @PostMapping("/")
    public ResponseEntity<?> addCandidateEducation(
            @RequestBody @Valid CandidateEducationDto candidateEducationDto
    ) {
        logger.info("Received request to add candidate education: {}", candidateEducationDto);
        var result = candidateEducationService.addCandidateEducation(candidateEducationDto);
        logger.info("Candidate education added successfully with ID: {}", result.getCandidateEducationId());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{candidateEducationId}")
    public ResponseEntity<?> updateCandidateEducation(
            @PathVariable Integer candidateEducationId,
            @RequestBody @Valid CandidateEducationDto candidateEducationDto
    ) {
        logger.info("Received request to update candidate education with ID: {}", candidateEducationId);
        var result = candidateEducationService.updateCandidateEducation(candidateEducationId, candidateEducationDto);
        logger.info("Candidate education updated successfully: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CandidateEducationDto>> getAllCandidateEducations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "candidateEducationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching all candidate educations - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        var response = candidateEducationService.getAllCandidateEducations(page, size, sortBy, sortDir);
        logger.info("Fetched {} candidate educations", response.getData().size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{candidateEducationId}")
    public ResponseEntity<?> getCandidateEducationById(
            @PathVariable Integer candidateEducationId
    ) {
        logger.info("Fetching candidate education by ID: {}", candidateEducationId);
        var result = candidateEducationService.getCandidateEducationById(candidateEducationId);
        logger.info("Fetched candidate education: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{candidateEducationId}")
    public ResponseEntity<?> deleteCandidateEducation(
            @PathVariable Integer candidateEducationId
    ) {
        logger.info("Request to delete candidate education with ID: {}", candidateEducationId);
        candidateEducationService.deleteCandidateEducation(candidateEducationId);
        logger.info("Deleted candidate education successfully for ID: {}", candidateEducationId);
        return new ResponseEntity<>(new ApiResponse(200, "Delete Successfully !", true),
                HttpStatus.OK);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<PaginatedResponse<CandidateEducationDto>> getCandidateEducationByCandidateId(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateEducationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching candidate educations for candidateId: {} (page={}, size={})", candidateId, page, size);
        var response = candidateEducationService.getCandidateEducationByCandidateId(candidateId, page, size, sortBy, sortDir);
        logger.info("Fetched {} records for candidateId: {}", response.getData().size(), candidateId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/degree/{degreeId}")
    public ResponseEntity<PaginatedResponse<CandidateEducationDto>> getCandidateEducationByDegreeId(
            @PathVariable Integer degreeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateEducationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching candidate educations for degreeId: {} (page={}, size={})", degreeId, page, size);
        var response = candidateEducationService.getCandidateEducationByDegreeId(degreeId, page, size, sortBy, sortDir);
        logger.info("Fetched {} records for degreeId: {}", response.getData().size(), degreeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/university/{universityId}")
    public ResponseEntity<PaginatedResponse<CandidateEducationDto>> getCandidateEducationByUniversityId(
            @PathVariable Integer universityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateEducationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching candidate educations for universityId: {} (page={}, size={})", universityId, page, size);
        var response = candidateEducationService.getCandidateEducationByUniversityId(universityId, page, size, sortBy, sortDir);
        logger.info("Fetched {} records for universityId: {}", response.getData().size(), universityId);
        return ResponseEntity.ok(response);
    }
}
