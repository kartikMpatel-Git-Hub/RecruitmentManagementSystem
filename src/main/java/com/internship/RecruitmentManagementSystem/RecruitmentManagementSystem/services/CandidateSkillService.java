package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateSkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateSkillRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.SkillRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateSkillServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CandidateSkillService implements CandidateSkillServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CandidateSkillService.class);

    private final CandidateRepository candidateRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateSkillData",allEntries = true)
    })
    public CandidateSkillDto addCandidateSKill(CandidateSkillDto candidateSkill) {
        logger.info("Adding candidate skill for candidateId: {}, skillId: {}",
                candidateSkill.getCandidate(),candidateSkill.getSkill());
        CandidateModel candidate = candidateRepository.findById(candidateSkill.getCandidate()).orElseThrow(
                ()->new ResourceNotFoundException("Candidate","CandidateId",candidateSkill.getCandidate().toString())
        );
        SkillModel skill = skillRepository.findById(candidateSkill.getSkill()).orElseThrow(
                ()->new ResourceNotFoundException("Skill","SkillId",candidateSkill.getSkill().toString())
        );

        CandidateSkillModel newCandidateSkill = new CandidateSkillModel();
        newCandidateSkill.setCandidate(candidate);
        newCandidateSkill.setSkill(skill);
        newCandidateSkill.setYearsOfExperience(candidateSkill.getYearsOfExperience());
        newCandidateSkill.setProficiencyLevel(candidateSkill.getProficiencyLevel());

        CandidateSkillModel savedCandidateSkill = candidateSkillRepository.save(newCandidateSkill);
        logger.info("Successfully added candidate Skill with ID: {}", savedCandidateSkill.getCandidateSkillId());
        return convertor(savedCandidateSkill);
    }

    @Override
    public CandidateSkillDto getCandidateSkillById(Integer candidateSkillId) {
        logger.info("Fetching candidate Skill With Id {} - ",candidateSkillId);
        CandidateSkillModel candidateSkill = candidateSkillRepository.findById(candidateSkillId).orElseThrow(
                ()->new ResourceNotFoundException("CandidateSkill","candidateSkillId",candidateSkillId.toString())
        );
        logger.info("Fetched {} candidate skill With Id ",candidateSkillId);
        return convertor(candidateSkill);
    }


    @Override
    @Cacheable(value = "candidateSkillData",key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateSkillDto> getAllData(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all candidate Skill - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
        Page<CandidateSkillModel> pageData = candidateSkillRepository.findAll(PageRequest.of(page, size, getSort(sortBy, sortDir)));
        PaginatedResponse<CandidateSkillDto> response = paginatedResponse(pageData);
        logger.info("Fetched {} candidate education records (Page {}/{})", response.getData().size(), page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateSkillData",allEntries = true),
    })
    public void deleteCandidateSkill(Integer candidateSKillId) {
        logger.info("Deleting candidate Skill with ID: {}", candidateSKillId);
        CandidateSkillModel existingCandidateSkill = candidateSkillRepository.findById(candidateSKillId)
                .orElseThrow(() -> {
                    logger.error("CandidateSkill not found with ID: {}", candidateSKillId);
                    return new ResourceNotFoundException("CandidateSkill", "candidateSkillId", candidateSKillId.toString());
                });
        candidateSkillRepository.delete(existingCandidateSkill);
        logger.info("Deleted candidate Skill successfully with ID: {}", candidateSKillId);
    }

    @Override
    @Cacheable(value = "candidateSkillData",key = "'candidateId_' + #candidateId + 'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateSkillDto> getCandidateSKillByCandidateId(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching candidate skill for candidateId: {}, Page: {}, Size: {}", candidateId, page, size);
        Page<CandidateSkillModel> pageData = candidateSkillRepository.findByCandidate_CandidateId(candidateId, PageRequest.of(page, size, getSort(sortBy, sortDir)));
        return paginatedResponse(pageData);
    }

    @Override
    @Cacheable(value = "candidateSkillData",key = "'skillId_' + #candidateId + 'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateSkillDto> getAllCandidatesBySkillId(Integer skillId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching candidates from skillId: {}, Page: {}, Size: {}", skillId, page, size);
        Page<CandidateSkillModel> pageData = candidateSkillRepository.findBySkill_SkillId(skillId, PageRequest.of(page, size, getSort(sortBy, sortDir)));
        return paginatedResponse(pageData);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "candidateSkillData",allEntries = true),
    })
    public CandidateSkillDto updateCandidateSkill(Integer candidateSkillId, CandidateSkillDto candidateSkill) {
        logger.info("Updating candidate skill with ID: {}", candidateSkillId);

        CandidateSkillModel existingCandidateSkill = candidateSkillRepository.findById(candidateSkillId)
                .orElseThrow(() -> {
                    logger.error("CandidateSkill not found with ID: {}", candidateSkillId);
                    return new ResourceNotFoundException("CandidateSkill", "candidateSkillId", candidateSkillId.toString());
                });

        if (existingCandidateSkill.getCandidate() != null) {
            CandidateModel newCandidate = candidateRepository.findById(candidateSkill.getCandidate())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate", "candidateId", candidateSkill.getCandidate().toString()));
            existingCandidateSkill.setCandidate(newCandidate);
        }
        if (candidateSkill.getSkill() != null) {
            SkillModel newSkill = skillRepository.findById(candidateSkill.getSkill())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill", "skillId", candidateSkill.getSkill().toString()));
            existingCandidateSkill.setSkill(newSkill);
        }

        if (candidateSkill.getProficiencyLevel() != null) {
            existingCandidateSkill.setProficiencyLevel(candidateSkill.getProficiencyLevel());
        }
        if (candidateSkill.getYearsOfExperience() != null) {
            existingCandidateSkill.setYearsOfExperience(candidateSkill.getYearsOfExperience());
        }

        CandidateSkillModel updatedCandidateSkill = candidateSkillRepository.save(existingCandidateSkill);
        logger.info("Updated candidate skill successfully with ID: {}", candidateSkillId);
        return convertor(updatedCandidateSkill);
    }

    private Sort getSort(String sortBy, String sortDir) {
        return sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }

    private CandidateSkillDto convertor(CandidateSkillModel entity) {
        CandidateSkillDto dto = new CandidateSkillDto();
        dto.setCandidateSkillId(entity.getCandidateSkillId());
        dto.setCandidateName(entity.getCandidate().getCandidateFirstName());
        dto.setCandidate(entity.getCandidate().getCandidateId());
        dto.setSkill(entity.getSkill().getSkillId());
        dto.setSkillName(entity.getSkill().getSkill());
        dto.setYearsOfExperience(entity.getYearsOfExperience());
        dto.setProficiencyLevel(entity.getProficiencyLevel());
        return dto;
    }

    private PaginatedResponse<CandidateSkillDto> paginatedResponse(Page<CandidateSkillModel> pageData) {
        PaginatedResponse<CandidateSkillDto> response = new PaginatedResponse<>();
        response.setData(pageData.getContent().stream().map(this::convertor).toList());
        response.setCurrentPage(pageData.getNumber());
        response.setPageSize(pageData.getSize());
        response.setTotalItems(pageData.getTotalElements());
        response.setTotalPages(pageData.getTotalPages());
        response.setLast(pageData.isLast());
        return response;
    }
}
