package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateSkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate-skills")
@RequiredArgsConstructor
public class CandidateSkillController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateSkillController.class);

    private final CandidateSkillService candidateSkillService;

    @PostMapping("/")
    public ResponseEntity<?> addCandidateSkill(@RequestBody CandidateSkillDto candidateSkillDto) {
        logger.info("Received request to add candidate skill: {}", candidateSkillDto);
        var result = candidateSkillService.addCandidateSKill(candidateSkillDto);
        logger.info("Candidate skill added successfully with ID: {}", result.getCandidateSkillId());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{candidateSkillId}")
    public ResponseEntity<?> updateCandidateSkill(
            @PathVariable Integer candidateSkillId,
            @RequestBody @Valid CandidateSkillDto candidateSkillDto
    ) {
        logger.info("Received request to update candidate skill with ID: {}", candidateSkillId);
        var result = candidateSkillService.updateCandidateSkill(candidateSkillId, candidateSkillDto);
        logger.info("Candidate skill updated successfully: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CandidateSkillDto>> getAllCandidateSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "candidateSkillId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching all candidate skills - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        var response = candidateSkillService.getAllData(page, size, sortBy, sortDir);
        logger.info("Fetched {} candidate skills", response.getData().size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{candidateSkillId}")
    public ResponseEntity<?> getCandidateSkillById(@PathVariable Integer candidateSkillId) {
        logger.info("Fetching candidate skill by ID: {}", candidateSkillId);
        var result = candidateSkillService.getCandidateSkillById(candidateSkillId);
        logger.info("Fetched candidate skill: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{candidateSkillId}")
    public ResponseEntity<?> deleteCandidateSkill(@PathVariable Integer candidateSkillId) {
        logger.info("Request to delete candidate skill with ID: {}", candidateSkillId);
        candidateSkillService.deleteCandidateSkill(candidateSkillId);
        logger.info("Deleted candidate skill successfully for ID: {}", candidateSkillId);
        return new ResponseEntity<>("Candidate Skill Deleted Successfully !", HttpStatus.OK);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<PaginatedResponse<CandidateSkillDto>> getCandidateSkillByCandidateId(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "candidateSkillId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching candidate skills for candidateId: {} (page={}, size={})", candidateId, page, size);
        var response = candidateSkillService.getCandidateSKillByCandidateId(candidateId, page, size, sortBy, sortDir);
        logger.info("Fetched {} records for candidateId: {}", response.getData().size(), candidateId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<PaginatedResponse<CandidateSkillDto>> getCandidatesBySkillId(
            @PathVariable Integer skillId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateSkillId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching candidates by skillId: {} (page={}, size={})", skillId, page, size);
        var response = candidateSkillService.getAllCandidatesBySkillId(skillId, page, size, sortBy, sortDir);
        logger.info("Fetched {} candidates for skillId: {}", response.getData().size(), skillId);
        return ResponseEntity.ok(response);
    }
}
