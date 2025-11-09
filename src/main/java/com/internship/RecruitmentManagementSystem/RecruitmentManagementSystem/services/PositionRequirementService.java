package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.PositionRequirementDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.PositionModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.PositionRequirementModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.PositionRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.PositionRequirementRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.SkillRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.PositionRequirementServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
@Deprecated
@RequiredArgsConstructor
public class PositionRequirementService implements PositionRequirementServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(PositionRequirementService.class);

    private final PositionRequirementRepository positionRequirementRepository;
    private final PositionRepository positionRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "positionData",allEntries = true)
    })
    public PositionRequirementDto addPositionRequirement(Integer positionId, PositionRequirementDto newRequirement) {
        logger.info("Adding new position requirement for positionId: {}", positionId);
        PositionModel position = positionRepository.findById(positionId).orElseThrow(
                ()->new ResourceNotFoundException("Position","positionId",positionId.toString())
        );
        SkillModel skill = skillRepository.findById(newRequirement.getPositionSkill().getSkillId()).orElseThrow(
                ()->new ResourceNotFoundException("Skill","skillId",newRequirement.getPositionSkill().getSkillId().toString())
        );
        PositionRequirementModel requirement = new PositionRequirementModel();
        requirement.setPositionRequirement(newRequirement.getPositionRequirement());
        requirement.setPositionRequiredSkill(skill);
        requirement.setPosition(position);

        positionRequirementRepository.save(requirement);
        logger.info("Position requirement added successfully for positionId: {} with skillId: {}", positionId, skill.getSkillId());
        return convertor(requirement);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "positionData",allEntries = true)
    })
    public PositionRequirementDto updatePositionRequirement(Integer positionRequirementId, PositionRequirementDto newPosition) {
        logger.info("Updating position requirement with ID: {}", positionRequirementId);
        PositionRequirementModel positionRequirement = positionRequirementRepository.findById(positionRequirementId).orElseThrow(
                ()->new ResourceNotFoundException("PositionRequirement","PositionRequirementId",positionRequirementId.toString())
        );

        if(newPosition.getPositionRequirement() != null){
            positionRequirement.setPositionRequirement(newPosition.getPositionRequirement());
        }
        if(newPosition.getPositionSkill() != null && newPosition.getPositionSkill().getSkillId() != null){
            SkillModel newSkill = skillRepository.findById(newPosition.getPositionSkill().getSkillId()).orElseThrow(
                    ()->new ResourceNotFoundException("Skill","skillId",newPosition.getPositionSkill().getSkillId().toString())
            );
            positionRequirement.setPositionRequiredSkill(newSkill);
        }
        PositionRequirementModel updatedPositionRequirement = positionRequirementRepository.save(positionRequirement);
        logger.info("Position requirement updated successfully with ID: {}", updatedPositionRequirement.getPositionRequirementId());
        return convertor(updatedPositionRequirement);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "positionData",allEntries = true)
    })
    public void removePositionRequirement(Integer positionRequirementId) {
        logger.info("Removing position requirement with ID: {}", positionRequirementId);
        PositionRequirementModel position = positionRequirementRepository.findById(positionRequirementId).orElseThrow(
                ()->new ResourceNotFoundException("PositionRequirement","positionRequirementId",positionRequirementId.toString())
        );
        positionRequirementRepository.delete(position);
        logger.info("Position requirement deleted successfully with ID: {}", positionRequirementId);
    }

    @Override
    @Cacheable(value = "positionData",key = "'positionRequirement_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<PositionRequirementDto> getPositionRequirements(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching position requirements for positionId: {}, page: {}, size: {}, sortBy: {}, sortDir: {}", positionId, page, size, sortBy, sortDir);
        PaginatedResponse<PositionRequirementDto> response = getPaginatedPositionRequirement(positionRequirementRepository.findAll(getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} position requirements", response.getData().size());
        return response;
    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PaginatedResponse<PositionRequirementDto> getPaginatedPositionRequirement(Page<PositionRequirementModel> pageResponse) {
        PaginatedResponse<PositionRequirementDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.getContent().stream().map(this::convertor).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        return response;
    }

    private PositionRequirementDto convertor(PositionRequirementModel entity){
        PositionRequirementDto dto = new PositionRequirementDto();
        dto.setPositionRequirementId(entity.getPositionRequirementId());
        dto.setPosition(entity.getPosition().getPositionId());

        SkillDto skill = new SkillDto();
        skill.setSkillId(entity.getPositionRequiredSkill().getSkillId());
        skill.setSkill(entity.getPositionRequiredSkill().getSkill());
        skill.setCreatedAt(entity.getPositionRequiredSkill().getCreatedAt());
        skill.setUpdatedAt(entity.getPositionRequiredSkill().getUpdatedAt());
        dto.setPositionSkill(skill);
        dto.setPositionRequirement(entity.getPositionRequirement());

        return dto;
    }
}
