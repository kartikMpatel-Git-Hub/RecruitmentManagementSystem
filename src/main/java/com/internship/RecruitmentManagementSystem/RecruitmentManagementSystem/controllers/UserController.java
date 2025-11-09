package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.UserUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.CandidateService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.FileService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final FileService fileService;
    private final CandidateService candidateService;

    @Value("${project.image}")
    private String path;

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        logger.info("Attempting to delete user with ID: {}", userId);

        if (userId == null) {
            logger.warn("Delete user attempt failed: User ID is null");
            return createErrorResponse("User Id is required!");
        }

        userService.deleteUser(userId);
        logger.info("Successfully deleted user with ID: {}", userId);
        return ResponseEntity.ok(new ApiResponse(200, "Deleted Successfully", true));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        logger.info("Fetching profile for user: {}", userName);

        UserResponseDto user = userService.getUserByUserName(userName);
        if (user.getRole().getRole().equalsIgnoreCase("CANDIDATE")) {
            var candidate = candidateService.getCandidateByUserId(user.getUserId());
            logger.info("Returning candidate profile for user ID: {}", user.getUserId());
            return ResponseEntity.ok(candidate);
        }

        logger.info("Returning user profile for user ID: {}", user.getUserId());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getProfile(@PathVariable Integer userId) {
        logger.info("Fetching user profile by ID: {}", userId);
        var user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserData(
            @RequestPart("user") @Valid UserUpdateDto request,
            @PathVariable Integer userId,
            @RequestPart(value = "image", required = false) MultipartFile userImage,
            @RequestPart(value = "accountInfo", required = false) AccountDetails accountDetails) {

        logger.info("Attempting to update user with ID: {}", userId);

        try {
            if (userImage != null && !userImage.isEmpty()) {
                String fileName = fileService.uploadImage(path, userImage);
                request.setUserImageUrl(fileName);
                logger.info("Uploaded user image for user ID: {}", userId);
            }

            var updatedUser = userService.updateUser(request, userId, accountDetails);
            logger.info("Successfully updated user with ID: {}", userId);
            return ResponseEntity.ok(updatedUser);

        } catch (InvalidImageFormateException e) {
            logger.error("Invalid image format for user ID {}: {}", userId, e.getMessage());
            return createErrorResponse("Invalid image format: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", userId, e.getMessage());
            return createErrorResponse("Something went wrong while updating user: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<UserResponseDto>> getAllUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching all users (page: {}, size: {}, sortBy: {}, sortDir: {})", page, size, sortBy, sortDir);
        return ResponseEntity.ok(userService.getUsers(page, size, sortBy, sortDir));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/non-candidates")
    public ResponseEntity<PaginatedResponse<UserResponseDto>> getAllNonCandidateUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching non-candidate users (page: {}, size: {})", page, size);
        return ResponseEntity.ok(userService.getNonCandidates(page, size, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    @GetMapping("/candidates")
    public ResponseEntity<PaginatedResponse<UserResponseDto>> getAllCandidateUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching candidate users (page: {}, size: {})", page, size);
        return ResponseEntity.ok(userService.getCandidates(page, size, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR','REVIEWER')")
    @GetMapping("/interviewers")
    public ResponseEntity<PaginatedResponse<UserResponseDto>> getAllInterviewerUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching Interviewer users (page: {}, size: {})", page, size);
        return ResponseEntity.ok(userService.getInterviewers(page, size, sortBy, sortDir));
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(String error) {
        return createErrorResponse(List.of(error));
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(List<String> errors) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, "Insufficient Data", errors, false));
    }
}
