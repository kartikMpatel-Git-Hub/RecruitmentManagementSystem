package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateEducationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateEducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate-educations")
@RequiredArgsConstructor
public class CandidateEducationController {

    private final CandidateEducationService candidateEducationService;

    @PostMapping("/")
    public ResponseEntity<?> addCandidateEducation(
            @RequestBody @Valid CandidateEducationDto candidateEducationDto
            ) {
        return new ResponseEntity<>(candidateEducationService.addCandidateEducation(candidateEducationDto),
                HttpStatus.CREATED);
    }

    @PutMapping("/{candidateEducationId}")
    public ResponseEntity<?> updateCandidateEducation(
            @PathVariable Integer candidateEducationId,
            @RequestBody @Valid CandidateEducationDto candidateEducationDto
    ) {
        return new ResponseEntity<>(candidateEducationService.updateCandidateEducation(candidateEducationId, candidateEducationDto),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CandidateEducationDto>> getAllCandidateEducations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "candidateEducationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(candidateEducationService.getAllCandidateEducations(page, size, sortBy, sortDir));
    }

    @GetMapping("/{candidateEducationId}")
    public ResponseEntity<?> getCandidateEducationById(
            @PathVariable Integer candidateEducationId
    ) {
        return new ResponseEntity<>(candidateEducationService.getCandidateEducationById(candidateEducationId),
                HttpStatus.OK);
    }

    @DeleteMapping("/{candidateEducationId}")
    public ResponseEntity<?> deleteCandidateEducation(
            @PathVariable Integer candidateEducationId
    ) {
        candidateEducationService.deleteCandidateEducation(candidateEducationId);
        return new ResponseEntity<>(new ApiResponse(200,"Delete Successfully !",true),
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
        return ResponseEntity.ok(candidateEducationService.getCandidateEducationByCandidateId(candidateId,page,size,sortBy,sortDir));
    }

    @GetMapping("/degree/{degreeId}")
    public ResponseEntity<PaginatedResponse<CandidateEducationDto>> getCandidateEducationByDegreeId(
            @PathVariable Integer degreeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateEducationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(candidateEducationService.getCandidateEducationByDegreeId(degreeId,page,size,sortBy,sortDir));
    }

    @GetMapping("/university/{universityId}")
    public ResponseEntity<PaginatedResponse<CandidateEducationDto>> getCandidateEducationByUniversityId(
            @PathVariable Integer universityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateEducationId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(candidateEducationService.getCandidateEducationByUniversityId(universityId,page,size,sortBy,sortDir));
    }

}
