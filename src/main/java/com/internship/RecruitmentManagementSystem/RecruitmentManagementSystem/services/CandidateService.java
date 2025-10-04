package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.CandidateRegistrationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateService implements CandidateServiceInterface {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CandidateRegistrationResponse register(CandidateDto candidateRequest) {

        validateCandidateRequest(candidateRequest);

        UserModel user = userRepository.save(getUserModel(candidateRequest));
        CandidateModel candidate = candidateRepository.save(getCandidateModel(candidateRequest, user));

        return new CandidateRegistrationResponse(
                candidate.getCandidateId(),
                user.getUserId(),
                user.getUserEmail(),
                "ROLE_CANDIDATE"
        );
    }

    private CandidateModel getCandidateModel(CandidateDto candidateRequest, UserModel savedUser) {
        CandidateModel candidateModel = new CandidateModel();
        candidateModel.setCandidateFirstName(candidateRequest.getCandidateFirstName());
        candidateModel.setCandidateMiddleName(candidateRequest.getCandidateMiddleName());
        candidateModel.setCandidateLastName(candidateRequest.getCandidateLastName());
        candidateModel.setCandidatePhoneNumber(candidateRequest.getCandidatePhoneNumber());
        candidateModel.setCandidateResumeUrl(candidateRequest.getCandidateResumeUrl());
        candidateModel.setCandidateGender(candidateRequest.getCandidateGender());
        candidateModel.setCandidateDateOfBirth(candidateRequest.getCandidateDateOfBirth());
        candidateModel.setCandidateAddress(candidateRequest.getCandidateAddress());
        candidateModel.setCandidateCity(candidateRequest.getCandidateCity());
        candidateModel.setCandidateState(candidateRequest.getCandidateState());
        candidateModel.setCandidateCountry(candidateRequest.getCandidateCountry());
        candidateModel.setCandidateZipCode(candidateRequest.getCandidateZipCode());
        candidateModel.setCandidateTotalExperienceInYears(candidateRequest.getCandidateTotalExperienceInYears());
        candidateModel.setUser(savedUser);
        return candidateModel;
    }

    private UserModel getUserModel(CandidateDto candidateRequest){

        RoleModel role = roleRepository.findByRole("ROLE_CANDIDATE").orElseThrow(
                ()-> new ResourceNotFoundException("ROLE","RoleName","ROLE_CANDIDATE")
        );

        UserModel userModel = new UserModel();
        userModel.setUserName(candidateRequest.getUserName());
        userModel.setUserEmail(candidateRequest.getUserEmail());
        userModel.setUserImageUrl(candidateRequest.getUserImageUrl());
        userModel.setUserPassword(passwordEncoder.encode(candidateRequest.getUserPassword()));
        userModel.setUserEnabled(true);
        userModel.getRoles().add(role);

        return userModel;
    }

    private void validateCandidateRequest(CandidateDto candidateRequest){
        if(userRepository.existsByUserEmail(candidateRequest.getUserEmail())){
            throw new ResourceAlreadyExistsException(
                    "User with email " + candidateRequest.getUserEmail() + " already exists");
        }

        if(candidateRepository.existsByUserUserEmail(candidateRequest.getUserEmail())){
            throw new ResourceAlreadyExistsException(
                    "Candidate with email " + candidateRequest.getUserEmail() + " already exists");
        }

        if(candidateRepository.existsByCandidatePhoneNumber(candidateRequest.getCandidatePhoneNumber())){
            throw new ResourceAlreadyExistsException(
                    "Candidate with Phone Number " + candidateRequest.getCandidatePhoneNumber() + " already exists");
        }
    }

}
