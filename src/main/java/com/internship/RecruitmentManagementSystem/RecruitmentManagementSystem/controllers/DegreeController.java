package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.DegreeDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiObjectResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiObjectsResponse;
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
import java.util.List;

@RestController
@RequestMapping("/degrees")
@CrossOrigin(origins = "http://localhost:5173")
//@PreAuthorize("hasRole('ADMIN')")
public class DegreeController {

    private static final Logger logger = LoggerFactory.getLogger(DegreeController.class);
    private static final String DEGREE_ADDED = "Degree added successfully";
    private static final String DEGREE_UPDATED = "Degree updated successfully";
    private static final String DEGREE_DELETED = "Degree deleted successfully";
    private static final String INVALID_DEGREE_ID = "Invalid degree ID";
    private static final String OPERATION_FAILED = "Operation failed";

    private final DegreeService degreeService;

    public DegreeController(DegreeService degreeService) {
        this.degreeService = degreeService;
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addDegree(@RequestBody @Valid DegreeDto degreeDto) {
        logger.info("Adding new degree: {}", degreeDto.getDegree());
        DegreeDto responseDegree = degreeService.addDegree(degreeDto);
        logger.info(DEGREE_ADDED);
        return new ResponseEntity<>(
                responseDegree,
                HttpStatus.CREATED);
    }

    @GetMapping("/{degreeId}")
    public ResponseEntity<?> getDegree(@PathVariable Integer degreeId){
        logger.info("Getting degree With Id : {}", degreeId);
        DegreeDto degree = degreeService.getDegree(degreeId);
        logger.info("Get degree With Id : {}", degreeId);
        return new ResponseEntity<>(degree,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<DegreeDto>> getDegrees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "degreeId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return new ResponseEntity<>(
                degreeService.getAllDegrees(page,size,sortBy,sortDir),
                HttpStatus.OK);
    }

    @DeleteMapping("/{degreeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDegrees(@PathVariable Integer degreeId){
        logger.info("Deleting degree With Id : {}", degreeId);
        degreeService.deleteDegree(degreeId);
        logger.info("Deleted degree With Id : {}", degreeId);
        return new ResponseEntity<>(
                new ApiResponse(200,"Delete Successfully !",true),
                HttpStatus.OK);
    }

    @PutMapping("/{degreeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDegrees(@PathVariable Integer degreeId,@RequestBody @Valid DegreeDto degreeDto){
        logger.info("Updating degree With Id : {}", degreeId);
        DegreeDto updatedDegree = degreeService.updateDegree(degreeId,degreeDto);
        logger.info("Updated degree With Id : {}", degreeId);
        return new ResponseEntity<>(
                updatedDegree,
                HttpStatus.OK);
    }
}
