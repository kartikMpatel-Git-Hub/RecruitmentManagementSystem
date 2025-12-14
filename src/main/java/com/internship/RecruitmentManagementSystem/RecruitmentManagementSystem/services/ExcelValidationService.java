package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other.CandidateRowData;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExcelValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelValidationService.class);

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RoleRepository roleRepository;

    public void validateUser(CandidateRowData data) {

        logger.info("Validating user data for row {}", data.getRowNumber());

        logger.debug("Checking username: {}", data.getUserName());
        if (isEmpty(data.getUserName())) {
            logger.warn("Row {}: Username is empty", data.getRowNumber());
            data.getError().addFieldError("userName", "Username cannot be empty");
        }

        logger.debug("Checking email: {}", data.getUserEmail());
        if (isEmpty(data.getUserEmail())) {
            logger.warn("Row {}: Email is empty", data.getRowNumber());
            data.getError().addFieldError("userEmail", "Email cannot be empty");
        } else if (!data.getUserEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            logger.warn("Row {}: Invalid email format {}", data.getRowNumber(), data.getUserEmail());
            data.getError().addFieldError("userEmail", "Invalid email format");
        } else if (userRepository.existsByUserEmail(data.getUserEmail())) {
            logger.error("Row {}: Email already exists in system {}", data.getRowNumber(), data.getUserEmail());
            data.getError().addFieldError("userEmail", "Email already exists");
        }

        logger.debug("Checking role: {}", data.getRoleName());
        if (isEmpty(data.getRoleName())) {
            logger.warn("Row {}: Role is empty", data.getRowNumber());
            data.getError().addFieldError("roleName", "Role is required");
        } else if (!roleRepository.existsByRole(data.getRoleName())) {
            logger.error("Row {}: Invalid role {}", data.getRowNumber(), data.getRoleName());
            data.getError().addFieldError("roleName", "Invalid role");
        }
    }

    public void validateCandidate(CandidateRowData data) {

        logger.info("Validating candidate data for row {}", data.getRowNumber());

        logger.debug("Checking first name: {}", data.getFirstName());
        if (isEmpty(data.getFirstName())) {
            logger.warn("Row {}: First name missing", data.getRowNumber());
            data.getError().addFieldError("candidateFirstName", "First name is required");
        }else if(data.getFirstName().length() > 30){
            logger.warn("Row {}: First name too long", data.getRowNumber());
            data.getError().addFieldError("candidateFirstName", "First name cannot exceed 30 characters");
        }

        logger.debug("Checking last name: {}", data.getLastName());
        if (isEmpty(data.getLastName())) {
            logger.warn("Row {}: Last name missing", data.getRowNumber());
            data.getError().addFieldError("candidateLastName", "Last name is required");
        }else if (data.getLastName().length() > 30){
            logger.warn("Row {}: Last name too long", data.getRowNumber());
            data.getError().addFieldError("candidateLastName", "Last name cannot exceed 30 characters");
        }

        logger.debug("Checking phone number: {}", data.getPhone());
        if (isEmpty(data.getPhone())) {
            logger.warn("Row {}: Phone missing", data.getRowNumber());
            data.getError().addFieldError("candidatePhoneNumber", "Phone number required");
        } else if (!data.getPhone().matches("\\d{10,12}")) {
            logger.warn("Row {}: Invalid phone number {}", data.getRowNumber(), data.getPhone());
            data.getError().addFieldError("candidatePhoneNumber", "Invalid phone number");
        } else if (candidateRepository.existsByCandidatePhoneNumber(data.getPhone())) {
            logger.error("Row {}: Phone number already exists {}", data.getRowNumber(), data.getPhone());
            data.getError().addFieldError("candidatePhoneNumber", "Phone number already exists");
        }

        logger.debug("Checking DOB: {}", data.getDob());
        if (data.getDob() == null) {
            logger.warn("Row {}: DOB missing", data.getRowNumber());
            data.getError().addFieldError("candidateDateOfBirth", "Date of birth required");
        }

        logger.debug("Checking city: {}", data.getCity());
        if (isEmpty(data.getCity())) {
            logger.warn("Row {}: City missing", data.getRowNumber());
            data.getError().addFieldError("candidateCity", "City required");
        }else if(data.getCity().length() > 30){
            logger.warn("Row {}: City name too long", data.getRowNumber());
            data.getError().addFieldError("candidateCity", "City name cannot exceed 30 characters");
        }

        logger.debug("Checking country: {}", data.getCountry());
        if (isEmpty(data.getCountry())) {
            logger.warn("Row {}: Country missing", data.getRowNumber());
            data.getError().addFieldError("candidateCountry", "Country required");
        }else if(data.getCountry().length() > 30){
            logger.warn("Row {}: Country name too long", data.getRowNumber());
            data.getError().addFieldError("candidateCountry", "Country name cannot exceed 30 characters");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
