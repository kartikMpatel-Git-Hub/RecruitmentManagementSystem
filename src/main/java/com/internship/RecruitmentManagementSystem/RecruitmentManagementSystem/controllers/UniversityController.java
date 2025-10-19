package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UniversityDto;
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
@RequestMapping("/university")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
@RequiredArgsConstructor
public class UniversityController {
    private static final Logger logger = LoggerFactory.getLogger(UniversityController.class);
    private final UniversityService universityService;

    @PutMapping("/")
    public ResponseEntity<?> addUniversity(@RequestBody UniversityDto universityDto){
        UniversityDto newUniversity = universityService.addUniversity(universityDto);
        return new ResponseEntity<>(newUniversity, HttpStatus.CREATED);
    }

    @GetMapping("/universityName/{universityName}")
    public ResponseEntity<?> getUniversityByName(@PathVariable String universityName){
        UniversityDto universityDto = universityService.getUniversityByName(universityName);
        return new ResponseEntity<>(universityDto,HttpStatus.OK);
    }

    @GetMapping("/{universityId}")
    public ResponseEntity<?> getUniversityById(@PathVariable Integer universityId){
        UniversityDto universityDto = universityService.getUniversityById(universityId);
        return new ResponseEntity<>(universityDto,HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUniversities(){
        List<UniversityDto> universities = universityService.getAllUniversities();
        return new ResponseEntity<>(universities,HttpStatus.OK);
    }

    @PutMapping("/{universityId}")
    public ResponseEntity<?> updateUniversity(@PathVariable Integer universityId,
                                              @RequestBody UniversityDto universityDto) {
        UniversityDto updatedUniversity = universityService.updateUniversity(universityId, universityDto);
        return new ResponseEntity<>(updatedUniversity, HttpStatus.OK);
    }

    @DeleteMapping("/{universityId}")
    public ResponseEntity<?> deleteUniversity(@PathVariable Integer universityId) {
        universityService.deleteUniversity(universityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
