package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.SkillService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skill")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class SkillController {
    private static final Logger logger = LoggerFactory.getLogger(SkillController.class);

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createNewSkill(@RequestBody @Valid SkillDto skillDto){
        logger.info("Attempting To Create New Skill");
        if(skillDto.getSkill()==null || skillDto.getSkill().isEmpty()){
            logger.error("Skill Name Is Empty");
            return ResponseEntity.badRequest().body("Skill Name Cannot Be Empty");
        }
        if(skillService.getBySkill(skillDto.getSkill()) == null){
            SkillDto skill = skillService.addSkill(skillDto);
            logger.info("Skill Created Successfully");
            return new ResponseEntity<>(skill, HttpStatus.CREATED);
        }else{
            logger.error("Skill Already Exists");
            return ResponseEntity.badRequest().body("Skill Already Exists");
        }
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillDto> getSkillById(@PathVariable Integer skillId){

        logger.info("Fetching Skill With Id : {}", skillId);
        SkillDto skill = skillService.getSkill(skillId);
        logger.info("Fetched Skill With Id : {}", skillId);
        return new ResponseEntity<>(skill,HttpStatus.OK);

    }

    @GetMapping("/")
    public ResponseEntity<List<SkillDto>> getSkills(){

        logger.info("Fetching Skills ");

        List<SkillDto> skills = skillService.getSkills();

        logger.info("Fetched All Skills ");

        return new ResponseEntity<>(skills,HttpStatus.OK);

    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<?> deleteSkill(@PathVariable Integer skillId){
        logger.info("Deleting Skill With Id : {}", skillId);
        skillService.deleteSkill(skillId);
        logger.info("Deleted Skill With Id : {}", skillId);
        return new ResponseEntity<>("Deleted Successfully !",HttpStatus.OK);
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateSkill(@PathVariable Integer skillId,@RequestBody @Valid SkillDto skillDto){
        logger.info("Updating Skill With Id : {}", skillId);
        SkillModel existingSkill = skillService.getBySkill(skillDto.getSkill());
        if (existingSkill != null && !existingSkill.getSkillId().equals(skillId)) {
            logger.error("Skill Already Exists");
            return ResponseEntity.badRequest().body("Skill Already Exists");
        }
        SkillDto updatedSkill = skillService.updateSkill(skillDto, skillId);
        logger.info("Updated Skill With Id : {}", skillId);
        return new ResponseEntity<>(updatedSkill,HttpStatus.OK);
    }

}
