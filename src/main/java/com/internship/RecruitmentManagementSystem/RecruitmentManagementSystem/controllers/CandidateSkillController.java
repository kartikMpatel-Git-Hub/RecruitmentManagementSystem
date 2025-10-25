package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateEducationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateSkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateSkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate-skills")
@RequiredArgsConstructor
public class CandidateSkillController {
    private final CandidateSkillService candidateSkillService;

    @PostMapping("/")
    public ResponseEntity<?> addCandidateSkill(
            @RequestBody CandidateSkillDto candidateSkillDto
            ){
        return new ResponseEntity<>(candidateSkillService.addCandidateSKill(candidateSkillDto),
                HttpStatus.CREATED);
    }

    @PutMapping("/{candidateSkillId}")
    public ResponseEntity<?> updateCandidateSkill(
            @PathVariable Integer candidateSkillId,
            @RequestBody @Valid CandidateSkillDto candidateSkillDto
    ){
        return new ResponseEntity<>(candidateSkillService.updateCandidateSkill(candidateSkillId,candidateSkillDto),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CandidateSkillDto>> getAllCandidateSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "candidateSkillId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(candidateSkillService.getAllData(page, size, sortBy, sortDir));
    }

    @GetMapping("/{candidateSkillId}")
    public ResponseEntity<?> getCandidateSkillById(
            @PathVariable Integer candidateSkillId
    ) {
        return new ResponseEntity<>(candidateSkillService.getCandidateSkillById(candidateSkillId),
                HttpStatus.OK);
    }

    @DeleteMapping("/{candidateSkillId}")
    public ResponseEntity<?> deleteCandidateSkill(
            @PathVariable Integer candidateSkillId
    ) {
        candidateSkillService.deleteCandidateSkill(candidateSkillId);
        return new ResponseEntity<>("Candidate SKill Deleted Successfully !",
                HttpStatus.OK);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<PaginatedResponse<CandidateSkillDto>> getCandidateSkillByCandidateId(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateSkillId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(candidateSkillService.getCandidateSKillByCandidateId(candidateId,page,size,sortBy,sortDir));
    }
    @GetMapping("/skill/{skillId}")
    public ResponseEntity<PaginatedResponse<CandidateSkillDto>> getCandidatesBySkillId(
            @PathVariable Integer skillId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateSkillId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(candidateSkillService.getAllCandidatesBySkillId(skillId,page,size,sortBy,sortDir));
    }

}
