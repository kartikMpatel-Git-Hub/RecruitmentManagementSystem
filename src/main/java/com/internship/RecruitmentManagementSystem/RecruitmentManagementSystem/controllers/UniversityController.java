package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UniversityDto;
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

import java.util.List;

@RestController
@RequestMapping("/universities")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UniversityController {
    private static final Logger logger = LoggerFactory.getLogger(UniversityController.class);
    private final UniversityService universityService;


    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    @PostMapping("/")
    public ResponseEntity<?> addUniversity(@RequestBody UniversityDto universityDto){
        UniversityDto newUniversity = universityService.addUniversity(universityDto);
        return new ResponseEntity<>(newUniversity, HttpStatus.CREATED);
    }

    @GetMapping("/university/{universityName}")
    public ResponseEntity<?> getUniversityByName(@PathVariable String universityName){
        UniversityDto universityDto = universityService.getUniversityByName(universityName);
        return new ResponseEntity<>(universityDto,HttpStatus.OK);
    }

    @GetMapping("/{universityId}")
    public ResponseEntity<?> getUniversityById(@PathVariable Integer universityId){
        UniversityDto universityDto = universityService.getUniversityById(universityId);
        return new ResponseEntity<>(universityDto,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<UniversityDto>> getAllUniversities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "universityId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        PaginatedResponse<UniversityDto> response = universityService.getAllUniversities(page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    @PutMapping("/{universityId}")
    public ResponseEntity<?> updateUniversity(@PathVariable Integer universityId,
                                              @RequestBody UniversityDto universityDto) {
        logger.info("Updating University : {}", universityId);
        UniversityDto updatedUniversity = universityService.updateUniversity(universityId, universityDto);
        return new ResponseEntity<>(updatedUniversity, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    @DeleteMapping("/{universityId}")
    public ResponseEntity<?> deleteUniversity(@PathVariable Integer universityId) {
        universityService.deleteUniversity(universityId);
        return new ResponseEntity<>(new ApiResponse(200,"Delete Successfully !",true),HttpStatus.OK);
    }

}
