package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.skill.CandidateSkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.skill.CandidateSkillUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateSkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateSkillServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CandidateSkillService implements CandidateSkillServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CandidateSkillService.class);

    private final Mapper mapper;
    private final ModelServiceInterface modelService;
    private final PaginatedResponseCreator paginatedResponseCreator;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateSkillData",allEntries = true)
    })
    public CandidateSkillResponseDto addCandidateSKill(CandidateSkillCreateDto candidateSkill) {
        logger.info("Adding candidate skill for candidateId: {}, skillId: {}",
                candidateSkill.getCandidate(),candidateSkill.getSkill());
        CandidateModel candidate = modelService.getCandidate(candidateSkill.getCandidate());
        SkillModel skill = modelService.getSkill(candidateSkill.getSkill());

        CandidateSkillModel newCandidateSkill = new CandidateSkillModel();
        newCandidateSkill.setCandidate(candidate);
        newCandidateSkill.setSkill(skill);
        newCandidateSkill.setYearsOfExperience(candidateSkill.getYearsOfExperience());
        newCandidateSkill.setProficiencyLevel(candidateSkill.getProficiencyLevel());

        CandidateSkillModel savedCandidateSkill = modelService.addCandidateSkill(newCandidateSkill);
        logger.info("Successfully added candidate Skill with ID: {}", savedCandidateSkill.getCandidateSkillId());
        return mapper.toDto(savedCandidateSkill, CandidateSkillResponseDto.class);
    }

    @Override
    public CandidateSkillResponseDto getCandidateSkillById(Integer candidateSkillId) {
        logger.info("Fetching candidate Skill With Id {} - ",candidateSkillId);
        CandidateSkillModel candidateSkill = modelService.getCandidateSkill(candidateSkillId);
        logger.info("Fetched {} candidate skill With Id ",candidateSkillId);
        return mapper.toDto(candidateSkill, CandidateSkillResponseDto.class);
    }

    @Override
    @Cacheable(value = "candidateSkillData",key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateSkillResponseDto> getAllData(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all candidate Skill - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<CandidateSkillResponseDto> response =
                    paginatedResponseCreator.getPaginatedResponse(modelService.getAllCandidateSkills(pageable),  CandidateSkillResponseDto.class);
        logger.info("Fetched {} candidate skill records (Page {}/{})", response.getData().size(), page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateSkillData",allEntries = true),
    })
    public void deleteCandidateSkill(Integer candidateSKillId) {
        logger.info("Deleting candidate Skill with ID: {}", candidateSKillId);
        CandidateSkillModel existingCandidateSkill = modelService.getCandidateSkill(candidateSKillId);
        modelService.removeCandidateSkill(existingCandidateSkill);
        logger.info("Deleted candidate Skill successfully with ID: {}", candidateSKillId);
    }

    @Override
    @Cacheable(value = "candidateSkillData",key = "'candidateId_' + #candidateId + 'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateSkillResponseDto> getCandidateSKillByCandidateId(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching candidate skill for candidateId: {}, Page: {}, Size: {}", candidateId, page, size);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<CandidateSkillResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                modelService.getCandidateSkillByCandidate(candidateId,pageable),
                        CandidateSkillResponseDto.class);
        logger.info("Fetched {} candidate skill By Candidate id : {} records (Page {}/{})", response.getData().size(),candidateId, page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Cacheable(value = "candidateSkillData",key = "'skillId_' + #candidateId + 'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateSkillResponseDto> getAllCandidatesBySkillId(Integer skillId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching candidates from skillId: {}, Page: {}, Size: {}", skillId, page, size);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<CandidateSkillResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                modelService.getCandidateSkillBySkill(skillId,pageable),  CandidateSkillResponseDto.class
                );
        logger.info("Fetched {} candidate skill By Skill id : {} records (Page {}/{})", response.getData().size(),skillId, page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "candidateSkillData",allEntries = true),
    })
    public CandidateSkillResponseDto updateCandidateSkill(Integer candidateSkillId, CandidateSkillUpdateDto candidateSkill) {
        logger.info("Updating candidate skill with ID: {}", candidateSkillId);

        CandidateSkillModel existingCandidateSkill = modelService.getCandidateSkill(candidateSkillId);

        if (candidateSkill.getSkill() != null) {
            SkillModel newSkill = modelService.getSkill(candidateSkill.getSkill());
            existingCandidateSkill.setSkill(newSkill);
        }

        if (candidateSkill.getProficiencyLevel() != null) {
            existingCandidateSkill.setProficiencyLevel(candidateSkill.getProficiencyLevel());
        }
        if (candidateSkill.getYearsOfExperience() != null) {
            existingCandidateSkill.setYearsOfExperience(candidateSkill.getYearsOfExperience());
        }

        CandidateSkillModel updatedCandidateSkill = modelService.addCandidateSkill(existingCandidateSkill);
        logger.info("Updated candidate skill successfully with ID: {}", candidateSkillId);
        return mapper.toDto(updatedCandidateSkill, CandidateSkillResponseDto.class);
    }

}
