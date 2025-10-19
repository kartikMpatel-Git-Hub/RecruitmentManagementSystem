package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.CandidateRegistrationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService implements CandidateServiceInterface {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final SkillService skillService;

    @Override
    @Transactional
    public CandidateRegistrationResponse register(UserModel userModel) {
        CandidateModel newCandidate = new CandidateModel();
        newCandidate.setUser(userModel);
        CandidateModel candidate = candidateRepository.save(newCandidate);
        return new CandidateRegistrationResponse(
                candidate.getCandidateId(),
                userModel.getUserId(),
                userModel.getUserEmail(),
                "ROLE_CANDIDATE"
        );
    }

    @Override
    public CandidateDto updateCandidate(CandidateDto newCandidate, Integer candidateId) {
        CandidateModel existingCandidate = candidateRepository.findById(candidateId).orElseThrow(
                ()-> new ResourceNotFoundException("Candidate","candidateId",candidateId.toString())
        );
        if (newCandidate.getCandidateFirstName() != null) existingCandidate.setCandidateFirstName(newCandidate.getCandidateFirstName());
        if (newCandidate.getCandidateMiddleName() != null)existingCandidate.setCandidateMiddleName(newCandidate.getCandidateMiddleName());
        if(newCandidate.getCandidateLastName() != null)existingCandidate.setCandidateLastName(newCandidate.getCandidateLastName());
        if(newCandidate.getCandidateGender() != null)existingCandidate.setCandidateGender(newCandidate.getCandidateGender());
        if(newCandidate.getCandidateDateOfBirth() != null)existingCandidate.setCandidateDateOfBirth(newCandidate.getCandidateDateOfBirth());
        if(newCandidate.getCandidateAddress() != null)existingCandidate.setCandidateAddress(newCandidate.getCandidateAddress());
        if (newCandidate.getCandidateCity() != null)existingCandidate.setCandidateCity(newCandidate.getCandidateCity());
        if(newCandidate.getCandidateState() != null)existingCandidate.setCandidateState(newCandidate.getCandidateState());
        if(newCandidate.getCandidateCountry() != null)existingCandidate.setCandidateCountry(newCandidate.getCandidateCountry());
        if(newCandidate.getCandidateZipCode() != null)existingCandidate.setCandidateZipCode(newCandidate.getCandidateZipCode());
        if(newCandidate.getCandidatePhoneNumber() != null)existingCandidate.setCandidatePhoneNumber(newCandidate.getCandidatePhoneNumber());
        if(newCandidate.getCandidateResumeUrl() != null)existingCandidate.setCandidateResumeUrl(newCandidate.getCandidateResumeUrl());
        if(newCandidate.getCandidateTotalExperienceInYears() != null)existingCandidate.setCandidateTotalExperienceInYears(newCandidate.getCandidateTotalExperienceInYears());
        if(newCandidate.getCandidateSkills() != null) {
            List<SkillModel> skills = newCandidate.getCandidateSkills().stream()
                    .map(dto -> {
                        SkillModel skill = skillService.getBySkill(dto.getSkill());
                        if(skill == null) skill = skillService.addSkillModel(dto);
                        return skill;
                    })
                    .collect(Collectors.toList());
            existingCandidate.setSkills(skills);
        }
        CandidateModel updatedCandidate = candidateRepository.save(existingCandidate);
        return convert(updatedCandidate);
    }

    @Override
    public Boolean deleteCandidate(Integer candidateId) {
        CandidateModel existingCandidate = candidateRepository.findById(candidateId).orElseThrow(
                ()-> new ResourceNotFoundException("Candidate","candidateId",candidateId.toString())
        );
        try{
            candidateRepository.delete(existingCandidate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<CandidateDto> getAllCandidates() {
        List<CandidateModel> candidates = candidateRepository.findAll();
        if(!candidates.isEmpty()){
            return candidates.stream().map(this::convert).toList();
        }
        return null;
    }

    @Override
    public CandidateDto getCandidate(Integer candidateId) {
        CandidateModel candidate = candidateRepository.findById(candidateId).orElseThrow(
                ()-> new ResourceNotFoundException("Candidate","candidateId",candidateId.toString())
        );
        if(candidate != null){
            return convert(candidate);
        }
        return null;
    }

    @Override
    public CandidateDto getCandidateByUserId(Integer userId) {
        CandidateModel candidate = candidateRepository.findByUserUserId(userId).orElseThrow(
                ()-> new ResourceNotFoundException("Candidate","userId",userId.toString())
        );
        if(candidate != null){
            return convert(candidate);
        }
        return null;
    }

    @Override
    public CandidateDto updateCandidateSkills(Integer candidateId, List<Integer> skillIds) {
        CandidateModel existingCandidate = candidateRepository.findById(candidateId).orElseThrow(
                ()-> new ResourceNotFoundException("Candidate","candidateId",candidateId.toString())
        );
        List<SkillModel> skills = skillIds.stream().map(skillId -> {
            SkillModel skill = skillService.getSkillById(skillId);
            if(skill == null){
                throw new ResourceNotFoundException("Skill","skillId",skillId.toString());
            }
            return skill;
        }).collect(Collectors.toList());
        existingCandidate.setSkills(skills);
        CandidateModel updatedCandidate = candidateRepository.save(existingCandidate);
        return convert(updatedCandidate);
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
        userModel.setRole(role);
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

    private CandidateDto convert(CandidateModel candidateModel){
        return modelMapper.map(candidateModel, CandidateDto.class);
    }

    private CandidateModel convert(CandidateDto candidateDto){
        return modelMapper.map(candidateDto, CandidateModel.class);
    }
}
