package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.CandidateRegistrationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService implements CandidateServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final SkillService skillService;

    @Override
    @Transactional
    @CacheEvict(value = "candidateData", allEntries = true)
    public CandidateRegistrationResponse register(UserModel userModel) {
        logger.info("Registering new candidate for user email: {}", userModel.getUserEmail());
        CandidateModel newCandidate = new CandidateModel();
        newCandidate.setUser(userModel);
        CandidateModel candidate = candidateRepository.save(newCandidate);
        logger.info("Candidate registered successfully with candidateId: {}", candidate.getCandidateId());

        return new CandidateRegistrationResponse(
                candidate.getCandidateId(),
                userModel.getUserId(),
                userModel.getUserEmail(),
                "ROLE_CANDIDATE"
        );
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateData", allEntries = true),
            @CacheEvict(value = "userCandidate", allEntries = true)
    })
    public CandidateDto updateCandidate(CandidateDto newCandidate, Integer candidateId) {
        logger.info("Updating candidate with ID: {}", candidateId);

        CandidateModel existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", candidateId);
                    return new ResourceNotFoundException("Candidate", "candidateId", candidateId.toString());
                });

        if (newCandidate.getCandidateFirstName() != null) existingCandidate.setCandidateFirstName(newCandidate.getCandidateFirstName());
        if (newCandidate.getCandidateMiddleName() != null) existingCandidate.setCandidateMiddleName(newCandidate.getCandidateMiddleName());
        if (newCandidate.getCandidateLastName() != null) existingCandidate.setCandidateLastName(newCandidate.getCandidateLastName());
        if (newCandidate.getCandidateGender() != null) existingCandidate.setCandidateGender(newCandidate.getCandidateGender());
        if (newCandidate.getCandidateDateOfBirth() != null) existingCandidate.setCandidateDateOfBirth(newCandidate.getCandidateDateOfBirth());
        if (newCandidate.getCandidateAddress() != null) existingCandidate.setCandidateAddress(newCandidate.getCandidateAddress());
        if (newCandidate.getCandidateCity() != null) existingCandidate.setCandidateCity(newCandidate.getCandidateCity());
        if (newCandidate.getCandidateState() != null) existingCandidate.setCandidateState(newCandidate.getCandidateState());
        if (newCandidate.getCandidateCountry() != null) existingCandidate.setCandidateCountry(newCandidate.getCandidateCountry());
        if (newCandidate.getCandidateZipCode() != null) existingCandidate.setCandidateZipCode(newCandidate.getCandidateZipCode());
        if (newCandidate.getCandidatePhoneNumber() != null) existingCandidate.setCandidatePhoneNumber(newCandidate.getCandidatePhoneNumber());
        if (newCandidate.getCandidateResumeUrl() != null) existingCandidate.setCandidateResumeUrl(newCandidate.getCandidateResumeUrl());
        if (newCandidate.getCandidateTotalExperienceInYears() != null) existingCandidate.setCandidateTotalExperienceInYears(newCandidate.getCandidateTotalExperienceInYears());

        CandidateModel updatedCandidate = candidateRepository.save(existingCandidate);
        logger.info("Candidate updated successfully with ID: {}", candidateId);
        return convert(updatedCandidate);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateData", allEntries = true),
            @CacheEvict(value = "userCandidate", allEntries = true)
    })
    public Boolean deleteCandidate(Integer candidateId) {
        logger.info("Deleting candidate with ID: {}", candidateId);

        CandidateModel existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", candidateId);
                    return new ResourceNotFoundException("Candidate", "candidateId", candidateId.toString());
                });

        try {
            candidateRepository.delete(existingCandidate);
            logger.info("Candidate deleted successfully with ID: {}", candidateId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete candidate with ID: {}", candidateId, e);
            return false;
        }
    }

    @Override
    @Cacheable(value = "candidateData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateDto> getAllCandidates(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidates - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CandidateModel> candidateDtoPage = candidateRepository.findAll(pageable);
        PaginatedResponse<CandidateDto> response = new PaginatedResponse<>();
        response.setData(candidateDtoPage.stream().map(this::convert).toList());
        response.setCurrentPage(candidateDtoPage.getNumber());
        response.setLast(candidateDtoPage.isLast());
        response.setPageSize(candidateDtoPage.getSize());
        response.setTotalItems(candidateDtoPage.getTotalElements());
        response.setTotalPages(candidateDtoPage.getTotalPages());

        logger.info("Fetched {} candidates (Page {}/{})", response.getData().size(), page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Cacheable(value = "userCandidate",key = "'id_' + #candidateId")
    public CandidateDto getCandidate(Integer candidateId) {
        logger.info("Fetching candidate with ID: {}", candidateId);
        CandidateModel candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", candidateId);
                    return new ResourceNotFoundException("Candidate", "candidateId", candidateId.toString());
                });
        return convert(candidate);
    }

    @Override
    @Cacheable(value = "userCandidate",key = "'userId_' + #candidateId")
    public CandidateDto getCandidateByUserId(Integer userId) {
        logger.info("Fetching candidate with userId: {}", userId);
        CandidateModel candidate = candidateRepository.findByUserUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Candidate not found with userId: {}", userId);
                    return new ResourceNotFoundException("Candidate", "userId", userId.toString());
                });
        return convert(candidate);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateData", allEntries = true),
            @CacheEvict(value = "userCandidate", allEntries = true)
    })

    public CandidateDto updateCandidateSkills(Integer candidateId, List<Integer> skillIds) {
        logger.info("Updating skills for candidate with ID: {}", candidateId);

        CandidateModel existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", candidateId);
                    return new ResourceNotFoundException("Candidate", "candidateId", candidateId.toString());
                });

        List<SkillModel> skills = skillIds.stream().map(skillId -> {
            SkillModel skill = skillService.getSkillById(skillId);
            if (skill == null) {
                logger.error("Skill not found with ID: {}", skillId);
                throw new ResourceNotFoundException("Skill", "skillId", skillId.toString());
            }
            return skill;
        }).collect(Collectors.toList());

        CandidateModel updatedCandidate = candidateRepository.save(existingCandidate);
        logger.info("Updated skills for candidate with ID: {}", candidateId);

        return convert(updatedCandidate);
    }

    private CandidateDto convert(CandidateModel candidateModel) {
        return modelMapper.map(candidateModel, CandidateDto.class);
    }

    private CandidateModel convert(CandidateDto candidateDto) {
        return modelMapper.map(candidateDto, CandidateModel.class);
    }
}
