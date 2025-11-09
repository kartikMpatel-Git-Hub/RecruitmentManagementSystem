package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.UniversityCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.UniversityUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.UniversityResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UniversityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/universities")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UniversityController {

    private static final Logger logger = LoggerFactory.getLogger(UniversityController.class);
    private final UniversityService universityService;

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @PostMapping("/")
    public ResponseEntity<?> addUniversity(@RequestBody UniversityCreateDto universityDto) {
        logger.info("Attempting to add new university: {}", universityDto.getUniversity());
        var newUniversity = universityService.addUniversity(universityDto);
        logger.info("University added successfully with ID: {}", newUniversity.getUniversityId());
        return new ResponseEntity<>(newUniversity, HttpStatus.CREATED);
    }

    @GetMapping("/university/{universityName}")
    public ResponseEntity<?> getUniversityByName(@PathVariable String universityName) {
        logger.info("Fetching university by name: {}", universityName);
        var universityDto = universityService.getUniversityByName(universityName);
        logger.info("Fetched university: {} (ID: {})", universityDto.getUniversity(), universityDto.getUniversityId());
        return new ResponseEntity<>(universityDto, HttpStatus.OK);
    }

    @GetMapping("/{universityId}")
    public ResponseEntity<?> getUniversityById(@PathVariable Integer universityId) {
        logger.info("Fetching university by ID: {}", universityId);
        var universityDto = universityService.getUniversityById(universityId);
        logger.info("Fetched university: {} (ID: {})", universityDto.getUniversity(), universityId);
        return new ResponseEntity<>(universityDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<UniversityResponseDto>> getAllUniversities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "universityId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching all universities (page: {}, size: {}, sortBy: {}, sortDir: {})", page, size, sortBy, sortDir);
        var response = universityService.getAllUniversities(page, size, sortBy, sortDir);
        logger.info("Fetched {} universities", response.getData().size());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @PutMapping("/{universityId}")
    public ResponseEntity<?> updateUniversity(@PathVariable Integer universityId,
                                              @RequestBody UniversityUpdateDto universityDto) {
        logger.info("Updating university with ID: {}", universityId);
        var updatedUniversity = universityService.updateUniversity(universityId, universityDto);
        logger.info("Updated university successfully: {} (ID: {})", updatedUniversity.getUniversity(), universityId);
        return new ResponseEntity<>(updatedUniversity, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @DeleteMapping("/{universityId}")
    public ResponseEntity<?> deleteUniversity(@PathVariable Integer universityId) {
        logger.info("Deleting university with ID: {}", universityId);
        universityService.deleteUniversity(universityId);
        logger.info("Deleted university successfully with ID: {}", universityId);
        return new ResponseEntity<>(new ApiResponse(200, "Delete Successfully!", true), HttpStatus.OK);
    }
}
