package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
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
import org.springframework.http.HttpStatus;
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
            return createErrorResponse("User Id Requires !");
        }

        userService.deleteUser(userId);
        logger.info("Successfully deleted user with ID: {}", userId);
        return new ResponseEntity<>(new ApiResponse(200, "Deleted", true), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        logger.info("Fetching profile for user: {}", userName);

        UserDto user = userService.getUserByUserName(userName);
        if(user.getRole().getRole().equals("CANDIDATE")){
            CandidateDto candidate = candidateService.getCandidateByUserId(user.getUserId());
            return new ResponseEntity<>(candidate, HttpStatus.OK);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Integer userId) {
        UserDto user = userService.getUser(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }



    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserData(@RequestPart("user") @Valid UserDto request,
                                            @PathVariable Integer userId,
                                            @RequestPart(value = "image", required = false) MultipartFile userImage,
                                            @RequestPart(value = "accountInfo", required = false) AccountDetails accountDetails) {
        logger.info("Attempting to update user with ID: {}", userId);

        try {
            if (userImage != null && !userImage.isEmpty()) {
                String fileName = fileService.uploadImage(path, userImage);
                request.setUserImageUrl(fileName);
            }

            UserDto updatedUser = userService.updateUser(request, userId, accountDetails);
            logger.info("Successfully updated user with ID: {}", userId);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (InvalidImageFormateException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return createErrorResponse("Invalid image format: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Something Went Wrong While Uploading !" + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDto>> getAllUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return new ResponseEntity<>(userService.getUsers(page, size, sortBy, sortDir),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/non-candidates")
    public ResponseEntity<PaginatedResponse<UserDto>> getAllNonCandidateUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return new ResponseEntity<>(userService.getNonCandidates(page, size, sortBy, sortDir),HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','HR')")
    @GetMapping("/candidates")
    public ResponseEntity<PaginatedResponse<UserDto>> getAllCandidateUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return new ResponseEntity<>(userService.getCandidates(page, size, sortBy, sortDir),HttpStatus.OK);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private ResponseEntity<?> createErrorResponse(String error) {
        return createErrorResponse(List.of(error));
    }

    private ResponseEntity<?> createErrorResponse(List<String> errors) {
        return new ResponseEntity<>(
                new ErrorResponse(400, "In Sufficient Data", errors, false),
                HttpStatus.BAD_REQUEST);
    }
}
