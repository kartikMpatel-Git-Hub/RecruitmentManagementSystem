package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeGetDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.position.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PositionController {

    private static final Logger log = LoggerFactory.getLogger(PositionController.class);
    private final PositionService positionService;

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> addPosition(@RequestBody PositionCreateDto newPosition) {
        log.info("Adding new position: {}", newPosition.getPositionTitle());
        var result = positionService.addPosition(newPosition);
        log.info("Position added successfully with ID: {}", result.getPositionId());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','REVIEWER')")
    public ResponseEntity<?> getAllPositions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "positionId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Fetching all positions (page: {}, size: {}, sortBy: {}, sortDir: {})", page, size, sortBy, sortDir);
        var response = positionService.getAllPositions(page, size, sortBy, sortDir);
        log.info("Fetched {} positions successfully", response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasAnyRole('RECRUITER')")
    public ResponseEntity<?> getAllPositionsByRecruiter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "positionId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer recruiterId = currentUser.getUserId();
        log.info("Fetching all positions By Recruiter {} (page: {}, size: {}, sortBy: {}, sortDir: {})",recruiterId, page, size, sortBy, sortDir);
        var response = positionService.getAllPositionsByRecruiter(recruiterId,page, size, sortBy, sortDir);
        log.info("Fetched {} positions By Recruiter {} successfully", response.getData().size(),recruiterId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<?> getPosition(@PathVariable Integer positionId) {
        log.info("Fetching position by ID: {}", positionId);
        var response = positionService.getPosition(positionId);
        log.info("Fetched position successfully: {}", response.getPositionTitle());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<?> countActivePositions() {
        log.info("Counting active positions...");
        var count = positionService.countActivePosition();
        log.info("Active positions count: {}", count);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PutMapping("/{positionId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> updatePosition(
            @PathVariable Integer positionId,
            @RequestBody @Valid PositionUpdateDto newPosition
    ) {
        log.info("Updating position ID: {}", positionId);
        var updated = positionService.updatePosition(positionId, newPosition);
        log.info("Updated position successfully: {}", updated.getPositionTitle());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{positionId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> deletePosition(@PathVariable Integer positionId) {
        log.info("Deleting position ID: {}", positionId);
        positionService.deletePosition(positionId);
        log.info("Deleted position successfully with ID: {}", positionId);
        return new ResponseEntity<>("Position With positionId : " + positionId + " Deleted !", HttpStatus.OK);
    }

    @PostMapping("/{positionId}/requirements")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> addPositionRequirement(
            @PathVariable Integer positionId,
            @RequestBody @Valid PositionRequirementCreateDto newPositionRequirement
    ) {
        log.info("Adding new requirement to position ID: {}", positionId);
        var result = positionService.addPositionRequirement(positionId, newPositionRequirement);
        log.info("Requirement added successfully to position ID: {}", positionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{positionId}/requirements/{positionRequirementId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> deletePositionRequirement(
            @PathVariable Integer positionId,
            @PathVariable Integer positionRequirementId
    ) {
        log.info("Deleting requirement ID: {} for position ID: {}", positionRequirementId, positionId);
        positionService.removePositionRequirement(positionRequirementId);
        log.info("Deleted requirement successfully with ID: {}", positionRequirementId);
        return new ResponseEntity<>("Position Requirement With positionRequirementId : " + positionRequirementId + " Deleted !", HttpStatus.OK);
    }

    @PutMapping("/{positionId}/requirements/{positionRequirementId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> updatePositionRequirement(
            @PathVariable Integer positionId,
            @PathVariable Integer positionRequirementId,
            @RequestBody @Valid PositionRequirementUpdateDto newPositionRequirement
    ) {
        log.info("Updating requirement ID: {} for position ID: {}", positionRequirementId, positionId);
        var updated = positionService.updatePositionRequirement(positionRequirementId, newPositionRequirement);
        log.info("Updated requirement successfully with ID: {}", positionRequirementId);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @PatchMapping("/{positionId}/rounds/{positionRoundId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> updatePositionRound(
            @PathVariable Integer positionId,
            @PathVariable Integer positionRoundId,
            @RequestBody @Valid PositionRoundUpdateDto positionRound
    ) {
        log.info("Updating Round ID: {} for position ID: {}", positionRoundId, positionId);
        var updated = positionService.changeRound(positionRoundId,positionRound);
        log.info("Updated Round successfully with ID: {}", positionRoundId);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping("/{positionId}/rounds")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> addPositionRound(
            @PathVariable Integer positionId,
            @RequestBody @Valid PositionRoundCreateDto positionRound
    ) {
        log.info("Add Round for position ID: {}", positionId);
        var updated = positionService.addRound(positionId,positionRound);
        log.info("Round Added successfully For PositionID: {}", updated.getPositionId());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{positionId}/rounds/{positionRoundId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> deletePositionRound(
            @PathVariable Integer positionId,
            @PathVariable Integer positionRoundId
    ) {
        log.info("Deleting Round ID: {} for position ID: {}", positionRoundId, positionId);
        positionService.deleteRound(positionRoundId);
        log.info("Deleted Round successfully with ID: {}", positionRoundId);
        return new ResponseEntity<>("Position Requirement With positionRequirementId : " + positionRoundId + " Deleted !", HttpStatus.OK);
    }

    @PatchMapping("/{positionId}/educations")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<?> updatePositionEducation(
            @PathVariable Integer positionId,
            @RequestBody List<DegreeGetDto> positionRequiredEducation
    ) {
        log.info("Updating education requirements for position ID: {}", positionId);
        var result = positionService.changeEducation(positionId, positionRequiredEducation);
        log.info("Updated education requirements successfully for position ID: {}", positionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{positionId}/requirements")
    public ResponseEntity<?> getPositionRequirements(
            @PathVariable Integer positionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "positionRequirementId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Fetching requirements for position ID: {} (page: {}, size: {}, sortBy: {}, sortDir: {})",
                positionId, page, size, sortBy, sortDir);
        var result = positionService.getPositionRequirements(positionId, page, size, sortBy, sortDir);
        log.info("Fetched {} requirements for position ID: {}", result.getData().size(), positionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
