package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/skills")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SkillController {

    private static final Logger logger = LoggerFactory.getLogger(SkillController.class);
    private final SkillService skillService;

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @PostMapping("/")
    public ResponseEntity<?> createNewSkill(@RequestBody @Valid SkillCreateDto skillDto) {
        logger.info("Attempting to create new skill: {}", skillDto.getSkill());

        if (skillDto.getSkill() == null || skillDto.getSkill().isEmpty()) {
            logger.error("Skill name is empty");
            return ResponseEntity.badRequest().body("Skill Name Cannot Be Empty");
        }

        var skill = skillService.addSkill(skillDto);
        logger.info("Skill created successfully with ID: {}", skill.getSkillId());
        return new ResponseEntity<>(skill, HttpStatus.CREATED);
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillResponseDto> getSkillById(@PathVariable Integer skillId) {
        logger.info("Fetching skill with ID: {}", skillId);
        var skill = skillService.getSkill(skillId);
        logger.info("Fetched skill successfully: {} (ID: {})", skill.getSkill(), skillId);
        return new ResponseEntity<>(skill, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<SkillResponseDto>> getSkills(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            @RequestParam(defaultValue = "skillId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching skills (page: {}, size: {}, sortBy: {}, sortDir: {})", page, size, sortBy, sortDir);
        var response = skillService.getSkills(page, size, sortBy, sortDir);
        logger.info("Fetched {} skills", response.getData().size());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @DeleteMapping("/{skillId}")
    public ResponseEntity<?> deleteSkill(@PathVariable Integer skillId) {
        logger.info("Deleting skill with ID: {}", skillId);
        skillService.deleteSkill(skillId);
        logger.info("Deleted skill successfully with ID: {}", skillId);
        return new ResponseEntity<>(new ApiResponse(200, "Delete Successfully!", true), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateSkill(@PathVariable Integer skillId, @RequestBody @Valid SkillUpdateDto skillDto) {
        logger.info("Updating skill with ID: {} to new value: {}", skillId, skillDto.getSkill());
        var updatedSkill = skillService.updateSkill(skillDto, skillId);
        logger.info("Updated skill successfully with ID: {}", skillId);
        return new ResponseEntity<>(updatedSkill, HttpStatus.OK);
    }
}
