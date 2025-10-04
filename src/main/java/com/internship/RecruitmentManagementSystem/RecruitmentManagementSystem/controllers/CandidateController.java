package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/register",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerCandidate(@RequestPart("candidate") @Valid CandidateDto candidateRegistrationDto,
                                               @RequestPart(value = "image",required = true) MultipartFile userImage,
                                               @RequestPart(value = "resume",required = true) MultipartFile resume
                                               )
    {
        logger.info("Registering new Candidate: {}", candidateRegistrationDto.getUserEmail());

        saveFiles(userImage,resume,candidateRegistrationDto);

        CandidateRegistrationResponse registerCandidate = candidateService.register(candidateRegistrationDto);

        logger.info("Candidate registered successfully: {}", registerCandidate.getUserEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(registerCandidate);
    }

    private void saveFiles(MultipartFile userImage, MultipartFile resume, CandidateDto candidateRegistrationDto){
        String imageUrl = saveFile(userImage);
        if (imageUrl == null) {
            throw new FailedProcessException("Failed to upload image");
        }
        candidateRegistrationDto.setUserImageUrl(imageUrl);
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
