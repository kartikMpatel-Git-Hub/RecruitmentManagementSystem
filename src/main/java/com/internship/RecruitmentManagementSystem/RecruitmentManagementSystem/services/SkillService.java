package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.SkillServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class SkillService implements SkillServiceInterface
{

    private static final Logger logger = LoggerFactory.getLogger(SkillService.class);

    private final Mapper mapper;
    private final PaginatedResponseCreator paginatedResponseCreator;
    private final ModelServiceInterface modelService;

    @Override
    @CacheEvict(value = "skillData", allEntries = true)
    public SkillResponseDto addSkill(SkillCreateDto skillDto) {
        logger.info("Attempting to add new skill: {}", skillDto.getSkill());

        if (modelService.existsSkill(skillDto.getSkill())) {
            logger.error("Skill '{}' already exists!", skillDto.getSkill());
            throw new ResourceAlreadyExistsException("Skill already exists");
        }
        SkillModel entity = mapper.toEntity(skillDto, SkillModel.class);
        SkillModel savedSkill = modelService.addSkill(entity);
        logger.info("Successfully added skill: {} (ID: {})", savedSkill.getSkill(), savedSkill.getSkillId());

        return mapper.toDto(savedSkill, SkillResponseDto.class);
    }

    @Override
    @Cacheable(value = "userSkill",key = "'id_'+#skillId")
    public SkillResponseDto getSkill(Integer skillId) {
        logger.info("Fetching skill with ID: {}", skillId);

        SkillModel skill = modelService.getSkill(skillId);

        logger.info("Skill found : {} (ID: {})", skill.getSkill(), skill.getSkillId());
        return mapper.toDto(skill, SkillResponseDto.class);
    }

    @Override
    @Cacheable(value = "skillData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<SkillResponseDto> getSkills(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching paginated skills - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        Page<SkillModel> pageResponse = modelService.findAllSkills(pageable);
        var response = paginatedResponseCreator.getPaginatedResponse(pageResponse, SkillResponseDto.class);
        logger.info("Successfully fetched {} skills (Page {}/{})", response.getData().size(), response.getCurrentPage() + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "skillData",allEntries = true),
            @CacheEvict(value = "userSkill",allEntries = true)
    })
    public SkillResponseDto updateSkill(SkillUpdateDto newSkill, Integer skillId) {
        logger.info("Updating skill with ID: {}", skillId);
        SkillModel Skill = getBySkill(newSkill.getSkill());
        if (Skill != null && !Skill.getSkillId().equals(skillId)) {
            logger.error("Skill '{}' already exists with a different ID: {}", newSkill.getSkill(), Skill.getSkillId());
            throw new ResourceAlreadyExistsException("Skill already exists");
        }
        SkillModel existingSkill = modelService.getSkill(skillId);
        if (newSkill.getSkill() != null && !newSkill.getSkill().trim().isEmpty()) {
            logger.debug("Updating skill name from '{}' to '{}'", existingSkill.getSkill(), newSkill.getSkill());
            existingSkill.setSkill(newSkill.getSkill());
        }
        SkillModel updatedSkill = modelService.addSkill(existingSkill);
        logger.info("Skill updated successfully: {} (ID: {})", updatedSkill.getSkill(), updatedSkill.getSkillId());
        return mapper.toDto(updatedSkill, SkillResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "skillData",allEntries = true),
            @CacheEvict(value = "userSkill",allEntries = true)
    })
    public void deleteSkill(Integer skillId) {
        logger.info("Deleting skill with ID: {}", skillId);

        SkillModel skill = modelService.getSkill(skillId);
        modelService.deleteSkill(skill);
        logger.info("Skill successfully deleted with ID: {}", skillId);
    }

    @Override
    @Cacheable(value = "userSkill",key = "'skillName_'+#skillName")
    public SkillModel getBySkill(String skillName) {
        logger.info("Searching for skill by name: {}", skillName);

        SkillModel skill = modelService.getSkill(skillName);
        if (skill == null) {
            logger.warn("No skill found with name: {}", skillName);
        } else {
            logger.info("Skill found: {} (ID: {})", skill.getSkill(), skill.getSkillId());
        }
        return skill;
    }
}
