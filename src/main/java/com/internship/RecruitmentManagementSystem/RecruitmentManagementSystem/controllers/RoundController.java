package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundResultDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.RoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rounds")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RoundController {

    private static final Logger log = LoggerFactory.getLogger(RoundController.class);
    private final RoundService roundService;

    @PostMapping("/applications/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> addRound(
            @PathVariable Integer applicationId,
            @RequestBody @Valid RoundCreateDto roundDto
    ) {
        log.info("Adding new round for application ID: {}", applicationId);
        var result = roundService.addRound(applicationId, roundDto);
        log.info("Round added successfully for application ID: {} with Round ID: {}", applicationId, result.getRoundId());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{roundId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> updateRound(
            @PathVariable Integer roundId,
            @RequestBody @Valid RoundUpdateDto roundDto
    ) {
        log.info("Updating round with ID: {}", roundId);
        var result = roundService.updateRound(roundId, roundDto);
        log.info("Round updated successfully with ID: {}", roundId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/pass/{roundId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> roundResult(
            @PathVariable Integer roundId,
            @RequestBody @Valid RoundResultDto roundResult
    ) {
        log.info("Pass round with ID: {}", roundId);
        var result = roundService.roundResult(roundId,roundResult);
        log.info("Round Pass successfully with ID: {}", roundId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{roundId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    public ResponseEntity<?> deleteRound(
            @PathVariable Integer roundId
    ) {
        log.info("Deleting round with ID: {}", roundId);
        roundService.removeRound(roundId);
        log.info("Round deleted successfully with ID: {}", roundId);
        return new ResponseEntity<>("Round With Id : " + roundId + " Deleted Successfully !", HttpStatus.OK);
    }

    @GetMapping("/{roundId}")
    public ResponseEntity<?> getRound(
            @PathVariable Integer roundId
    ) {
        log.info("Fetching round with ID: {}", roundId);
        var response = roundService.getRound(roundId);
        log.info("Fetched round successfully with ID: {}", roundId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<?> getCandidateRounds(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "roundId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("Fetching rounds for candidate ID: {} (page: {}, size: {}, sortBy: {}, sortDir: {})",
                candidateId, page, size, sortBy, sortDir);
        var result = roundService.candidateRounds(candidateId, page, size, sortBy, sortDir);
        log.info("Fetched {} rounds for candidate ID: {}", result.getData().size(), candidateId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<?> getApplicationRounds(
            @PathVariable Integer applicationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "roundId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("Fetching rounds for application ID: {} (page: {}, size: {}, sortBy: {}, sortDir: {})",
                applicationId, page, size, sortBy, sortDir);
        var result = roundService.applicationRound(applicationId, page, size, sortBy, sortDir);
        log.info("Fetched {} rounds for application ID: {}", result.getData().size(), applicationId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
