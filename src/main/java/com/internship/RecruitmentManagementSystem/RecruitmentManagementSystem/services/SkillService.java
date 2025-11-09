package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.SkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.SkillUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.SkillRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.SkillServiceInterface;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class SkillService implements SkillServiceInterface
{

    private static final Logger logger = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(value = "skillData", allEntries = true)
    public SkillResponseDto addSkill(SkillCreateDto skillDto) {
        logger.info("Attempting to add new skill: {}", skillDto.getSkill());

        if (skillRepository.existsBySkill(skillDto.getSkill())) {
            logger.error("Skill '{}' already exists!", skillDto.getSkill());
            throw new ResourceAlreadyExistsException("Skill already exists");
        }

        SkillModel savedSkill = skillRepository.save(convertor(skillDto));
        logger.info("Successfully added skill: {} (ID: {})", savedSkill.getSkill(), savedSkill.getSkillId());

        return convertor(savedSkill);
    }

//    @Override
//    public SkillModel addSkillModel(SkillCreateDto skillDto) {
//        return convertor(skillDto);
//    }

    @Override
    @Cacheable(value = "userSkill",key = "'id_'+#skillId")
    public SkillResponseDto getSkill(Integer skillId) {
        logger.info("Fetching skill with ID: {}", skillId);

        SkillModel skill = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    logger.error("Skill not found with ID: {}", skillId);
                    return new ResourceNotFoundException("Skill", "skillId", skillId.toString());
                });

        logger.info("Skill found: {} (ID: {})", skill.getSkill(), skill.getSkillId());
        return convertor(skill);
    }

    @Override
    public SkillModel getSkillById(Integer skillId) {
        logger.info("Fetching SkillModel by ID: {}", skillId);

        SkillModel skill = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    logger.error("SkillModel not found with ID: {}", skillId);
                    return new ResourceNotFoundException("Skill", "skillId", skillId.toString());
                });

        logger.info("SkillModel found: {} (ID: {})", skill.getSkill(), skill.getSkillId());
        return skill;
    }

    @Override
    @Cacheable(value = "skillData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<SkillResponseDto> getSkills(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching paginated skills - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SkillModel> pageResponse = skillRepository.findAll(pageable);

        PaginatedResponse<SkillResponseDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.getContent().stream().map(this::convertor).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());

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
        SkillModel existingSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    logger.error("Skill not found for update, ID: {}", skillId);
                    return new ResourceNotFoundException("Skill", "skillId", skillId.toString());
                });

        if (newSkill.getSkill() != null && !newSkill.getSkill().trim().isEmpty()) {
            logger.debug("Updating skill name from '{}' to '{}'", existingSkill.getSkill(), newSkill.getSkill());
            existingSkill.setSkill(newSkill.getSkill());
        }

        SkillModel updatedSkill = skillRepository.save(existingSkill);
        logger.info("Skill updated successfully: {} (ID: {})", updatedSkill.getSkill(), updatedSkill.getSkillId());

        return convertor(updatedSkill);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "skillData",allEntries = true),
            @CacheEvict(value = "userSkill",allEntries = true)
    })
    public void deleteSkill(Integer skillId) {
        logger.info("Deleting skill with ID: {}", skillId);

        SkillModel skill = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    logger.error("Skill not found for deletion, ID: {}", skillId);
                    return new ResourceNotFoundException("Skill", "skillId", skillId.toString());
                });

        skillRepository.delete(skill);
        logger.info("Skill successfully deleted with ID: {}", skillId);
    }

    @Override
    @Cacheable(value = "userSkill",key = "'skillName_'+#skillName")
    public SkillModel getBySkill(String skillName) {
        logger.info("Searching for skill by name: {}", skillName);

        SkillModel skill = skillRepository.findBySkill(skillName).orElse(null);
        if (skill == null) {
            logger.warn("No skill found with name: {}", skillName);
        } else {
            logger.info("Skill found: {} (ID: {})", skill.getSkill(), skill.getSkillId());
        }

        return skill;
    }

    private SkillResponseDto convertor(SkillModel skill) {
        return modelMapper.map(skill, SkillResponseDto.class);
    }

    private SkillModel convertor(SkillCreateDto skill) {
        return modelMapper.map(skill, SkillModel.class);
    }
}
