package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.DegreeDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.DegreeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/degrees")
@CrossOrigin(origins = "http://localhost:5173")
public class DegreeController {

    private static final Logger logger = LoggerFactory.getLogger(DegreeController.class);

    private static final String DEGREE_ADDED = "Degree added successfully";
    private static final String DEGREE_UPDATED = "Degree updated successfully";
    private static final String DEGREE_DELETED = "Degree deleted successfully";

    private final DegreeService degreeService;

    public DegreeController(DegreeService degreeService) {
        this.degreeService = degreeService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> addDegree(@RequestBody @Valid DegreeDto degreeDto) {
        logger.info("Received request to add new degree: {}", degreeDto.getDegree());
            DegreeDto responseDegree = degreeService.addDegree(degreeDto);
            logger.info(DEGREE_ADDED + " with ID: {}", responseDegree.getDegreeId());
            return new ResponseEntity<>(responseDegree, HttpStatus.CREATED);
    }

    @GetMapping("/{degreeId}")
    public ResponseEntity<?> getDegree(@PathVariable Integer degreeId) {
        logger.info("Fetching degree with ID: {}", degreeId);
        try {
            DegreeDto degree = degreeService.getDegree(degreeId);
            logger.info("Successfully fetched degree: {}", degree);
            return new ResponseEntity<>(degree, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching degree with ID {}: {}", degreeId, e.getMessage(), e);
            return new ResponseEntity<>("Degree not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<DegreeDto>> getDegrees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "degreeId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching degrees - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<DegreeDto> response = degreeService.getAllDegrees(page, size, sortBy, sortDir);
        logger.info("Fetched {} degrees successfully", response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{degreeId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> deleteDegrees(@PathVariable Integer degreeId) {
        logger.info("Deleting degree with ID: {}", degreeId);
        try {
            degreeService.deleteDegree(degreeId);
            logger.info(DEGREE_DELETED + " (ID: {})", degreeId);
            return new ResponseEntity<>(new ApiResponse(200, "Delete Successfully!", true), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while deleting degree with ID {}: {}", degreeId, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse(500, "Error while deleting degree!", false),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{degreeId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> updateDegrees(@PathVariable Integer degreeId, @RequestBody @Valid DegreeDto degreeDto) {
        logger.info("Updating degree with ID: {}", degreeId);
        try {
            DegreeDto updatedDegree = degreeService.updateDegree(degreeId, degreeDto);
            logger.info(DEGREE_UPDATED + " (ID: {})", degreeId);
            return new ResponseEntity<>(updatedDegree, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating degree with ID {}: {}", degreeId, e.getMessage(), e);
            return new ResponseEntity<>("Failed to update degree", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
