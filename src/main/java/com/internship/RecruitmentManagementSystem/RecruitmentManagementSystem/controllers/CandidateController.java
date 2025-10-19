package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.CandidateRegistrationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.FileService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/candidate")
@CrossOrigin(origins = "http://localhost:5173")
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;
    private final FileService fileService;

    public CandidateController(CandidateService candidateService, FileService fileService) {
        this.candidateService = candidateService;
        this.fileService = fileService;
    }

    @Value("${project.image}")
    private String path;

    @PutMapping(value = "/{candidateId}",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CandidateDto> updateCandidate(
            @PathVariable Integer candidateId,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart("candidate") @Valid CandidateDto existingCandidate
    ) {
        if(resume != null && !resume.isEmpty())
            saveFiles(resume, existingCandidate);
        CandidateDto updatedCandidate = candidateService.updateCandidate(existingCandidate, candidateId);
        return new ResponseEntity<>(updatedCandidate, HttpStatus.OK);
    }

    @PatchMapping(value = "/{candidateId}")
    public ResponseEntity<?> updateSkills(@PathVariable Integer candidateId,
                                          @RequestBody List<Integer> skillIds){
        CandidateDto candidateWithUpdatedSkills = candidateService.updateCandidateSkills(candidateId, skillIds);
        return new ResponseEntity<>(candidateWithUpdatedSkills,HttpStatus.OK);
    }

    @DeleteMapping(value = "/{candidateId}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Integer candidateId) {
        Boolean isDeleted = candidateService.deleteCandidate(candidateId);
        if (isDeleted) {
            return new ResponseEntity<>("Delete Candidate !", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed TO Delete Candidate With Id : " + candidateId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> getCandidates(){
        List<CandidateDto> candidates = candidateService.getAllCandidates();
        return new ResponseEntity<>(candidates, HttpStatus.OK);
    }

    @GetMapping(value = "/{candidateId}")
    public ResponseEntity<?> getCandidate(@PathVariable Integer candidateId){
        CandidateDto candidate = candidateService.getCandidate(candidateId);
        return new ResponseEntity<>(candidate,HttpStatus.OK);
    }

    private void saveFiles( MultipartFile resume, CandidateDto candidateRegistrationDto){
        String resumeUrl = saveFile(resume);
        if (resumeUrl == null) {
            throw new FailedProcessException("Failed to upload resume");
        }
        candidateRegistrationDto.setCandidateResumeUrl(resumeUrl);
    }

    private String saveFile(MultipartFile file){
        try {
            return fileService.uploadImage(path, file);
        } catch (InvalidImageFormateException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
