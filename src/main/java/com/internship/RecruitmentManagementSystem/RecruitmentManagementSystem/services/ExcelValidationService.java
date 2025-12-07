package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other.CandidateRowData;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExcelValidationService {
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RoleRepository roleRepository;

    public void validateUser(CandidateRowData data) {
        if (isEmpty(data.getUserName())) {
            data.getError().addFieldError("userName", "Username cannot be empty");
        }
        if (isEmpty(data.getUserEmail())) {
            data.getError().addFieldError("userEmail", "Email cannot be empty");
        } else if (!data.getUserEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            data.getError().addFieldError("userEmail", "Invalid email format");
        } else if (userRepository.existsByUserEmail(data.getUserEmail())) {
            data.getError().addFieldError("userEmail", "Email already exists");
        }
        if (isEmpty(data.getRoleName())) {
            data.getError().addFieldError("roleName", "Role is required");
        } else if (!roleRepository.existsByRole(data.getRoleName())) {
            data.getError().addFieldError("roleName", "Invalid role");
        }
    }

    public void validateCandidate(CandidateRowData data) {
        if (isEmpty(data.getFirstName())) {
            data.getError().addFieldError("candidateFirstName", "First name is required");
        }
        if (isEmpty(data.getLastName())) {
            data.getError().addFieldError("candidateLastName", "Last name is required");
        }
        if (isEmpty(data.getPhone())) {
            data.getError().addFieldError("candidatePhoneNumber", "Phone number required");
        } else if (!data.getPhone().matches("\\d{10,12}")) {
            data.getError().addFieldError("candidatePhoneNumber", "Invalid phone number");
        } else if (candidateRepository.existsByCandidatePhoneNumber(data.getPhone())) {
            data.getError().addFieldError("candidatePhoneNumber", "Phone number already exists");
        }
        if (data.getDob() == null) {
            data.getError().addFieldError("candidateDateOfBirth", "Date of birth required");
        }
        if (isEmpty(data.getCity())) {
            data.getError().addFieldError("candidateCity", "City required");
        }
        if (isEmpty(data.getCountry())) {
            data.getError().addFieldError("candidateCountry", "Country required");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
