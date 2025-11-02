package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.PositionServiceInterface;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService implements PositionServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);
    private final PositionRepository positionRepository;
    private final AuthService authService;
    private final SkillRepository skillRepository;
    private final PositionRequirementRepository positionRequirementRepository;
    private final DegreeRepository degreeRepository;

    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public PositionDto addPosition(PositionDto newPosition) {
        logger.info("Attempting to add a new position: {}", newPosition.getPositionTitle());
        PositionStatusModel positionStatus = new PositionStatusModel();
        positionStatus.setPositionStatus(newPosition.getPositionStatus().getStatus());
        positionStatus.setPositionStatusReason(newPosition.getPositionStatus().getPositionStatusReason());
        logger.debug("Position status created: {}", newPosition.getPositionStatus().getStatus());

        UserModel currentUser = authService.getCurrentUser();
        logger.debug("Current user creating position: {}", currentUser.getUserId());

        PositionModel position = new PositionModel();
        position.setCreatedBy(currentUser);
        position.setPositionTitle(newPosition.getPositionTitle());
        position.setPositionCriteria(newPosition.getPositionCriteria());
        position.setPositionDescription(newPosition.getPositionDescription());
        position.setPositionTotalOpening(newPosition.getPositionTotalOpening());
        position.setPositionLocation(newPosition.getPositionLocation());
        position.setPositionType(newPosition.getPositionType());
        position.setPositionSalary(newPosition.getPositionSalary());
        position.setPositionStatus(positionStatus);
        position.setPositionLanguage(newPosition.getPositionLanguage());

        PositionModel savedPosition = positionRepository.save(position);
        logger.info("Position created successfully with ID: {}", savedPosition.getPositionId());

        List<PositionRequirementModel> requirements = new ArrayList<>();
        newPosition.getPositionRequirements().forEach(requirement -> {
            SkillModel requiredSkill = skillRepository.findById(requirement.getPositionSkill().getSkillId())
                    .orElseThrow(() -> {
                        logger.error("Skill not found: {}", requirement.getPositionSkill().getSkillId());
                        return new ResourceNotFoundException("Skill", "skillId",
                                requirement.getPositionSkill().getSkillId().toString());
                    });
            PositionRequirementModel newRequirement = new PositionRequirementModel();
            newRequirement.setPosition(savedPosition);
            newRequirement.setPositionRequiredSkill(requiredSkill);
            newRequirement.setPositionRequirement(requirement.getPositionRequirement());
            PositionRequirementModel savedRequirement = positionRequirementRepository.save(newRequirement);
            requirements.add(savedRequirement);
        });
        logger.debug("Position requirements added successfully for position ID: {}", savedPosition.getPositionId());

        List<DegreeModel> education = newPosition.getPositionRequiredEducations().stream().map(
                degree -> degreeRepository.findById(degree.getDegreeId()).orElseThrow(() -> {
                    logger.error("Degree not found: {}", degree.getDegreeId());
                    return new ResourceNotFoundException("Degree", "degreeId", degree.getDegreeId().toString());
                })
        ).toList();
        position.setPositionRequiredEducations(education);
        logger.info("Education requirements set for position ID: {}", savedPosition.getPositionId());

        logger.info("Position added successfully: {}", savedPosition.getPositionTitle());
        return converter(position);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public PositionDto updatePosition(Integer positionId, PositionDto newPosition) {
        logger.info("Attempting to update position with ID: {}", positionId);
        PositionModel oldPosition = positionRepository.findById(positionId).orElseThrow(() -> {
            logger.error("Position not found for ID: {}", positionId);
            return new ResourceNotFoundException("Position", "PositionId", positionId.toString());
        });

        if (newPosition.getPositionDescription() != null) oldPosition.setPositionDescription(newPosition.getPositionDescription());
        if (newPosition.getPositionCriteria() != null) oldPosition.setPositionCriteria(newPosition.getPositionCriteria());
        if (newPosition.getPositionTitle() != null) oldPosition.setPositionTitle(newPosition.getPositionTitle());
        if (newPosition.getPositionTotalOpening() != null) oldPosition.setPositionTotalOpening(newPosition.getPositionTotalOpening());
        if (newPosition.getPositionStatus() != null && newPosition.getPositionStatus().getStatus() != null)
            oldPosition.getPositionStatus().setPositionStatus(newPosition.getPositionStatus().getStatus());
        if (newPosition.getPositionStatus() != null && newPosition.getPositionStatus().getPositionStatusReason() != null)
            oldPosition.getPositionStatus().setPositionStatusReason(newPosition.getPositionStatus().getPositionStatusReason());
        if (newPosition.getPositionLocation() != null) oldPosition.setPositionLocation(newPosition.getPositionLocation());
        if (newPosition.getPositionSalary() != null) oldPosition.setPositionSalary(newPosition.getPositionSalary());
        if (newPosition.getPositionType() != null) oldPosition.setPositionType(newPosition.getPositionType());
        if (newPosition.getPositionLanguage() != null) oldPosition.setPositionLanguage(newPosition.getPositionLanguage());

        PositionModel updatedPosition = positionRepository.save(oldPosition);
        logger.info("Position updated successfully for ID: {}", positionId);
        return converter(updatedPosition);
    }

    @Override
    @Cacheable(value = "positionData", key = "'positionId_'+#positionId")
    public PositionDto getPosition(Integer positionId) {
        logger.debug("Fetching position with ID: {}", positionId);
        PositionModel position = positionRepository.findById(positionId).orElseThrow(() -> {
            logger.error("Position not found with ID: {}", positionId);
            return new ResourceNotFoundException("Position", "PositionId", positionId.toString());
        });
        logger.info("Fetched position successfully with ID: {}", positionId);
        return converter(position);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public void deletePosition(Integer positionId) {
        logger.warn("Deleting position with ID: {}", positionId);
        PositionModel position = positionRepository.findById(positionId).orElseThrow(() -> {
            logger.error("Position not found for deletion: {}", positionId);
            return new ResourceNotFoundException("Position", "PositionId", positionId.toString());
        });
        positionRepository.delete(position);
        logger.info("Position deleted successfully with ID: {}", positionId);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public PositionDto changeEducation(Integer positionId, List<DegreeDto> positionEducation) {
        logger.info("Changing education for position ID: {}", positionId);
        PositionModel existingPosition = positionRepository.findById(positionId)
                .orElseThrow(() -> {
                    logger.error("Position not found for education update: {}", positionId);
                    return new ResourceNotFoundException("Position", "positionId", positionId.toString());
                });

        List<DegreeModel> updatedDegrees = positionEducation.stream()
                .map(education -> degreeRepository.findById(education.getDegreeId())
                        .orElseThrow(() -> {
                            logger.error("Degree not found: {}", education.getDegreeId());
                            return new ResourceNotFoundException("Degree", "degreeId", education.getDegreeId().toString());
                        }))
                .toList();

        existingPosition.getPositionRequiredEducations().clear();
        existingPosition.getPositionRequiredEducations().addAll(updatedDegrees);

        PositionModel updatedPosition = positionRepository.save(existingPosition);
        logger.info("Education updated successfully for position ID: {}", positionId);
        return converter(updatedPosition);
    }

    @Override
    @Cacheable(value = "positionData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<PositionDto> getAllPositions(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching all positions page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        return getPaginatedPosition(positionRepository.findAll(getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Cacheable(value = "positionData", key = "'position_count'")
    public Long countActivePosition() {
        logger.debug("Counting all active positions");
        Long count = positionRepository.countActivePosition();
        logger.info("Total active positions: {}", count);
        return count;
    }

    private PaginatedResponse<PositionDto> getPaginatedPosition(Page<PositionModel> pageResponse) {
        logger.debug("Building paginated response for {} positions", pageResponse.getContent().size());
        PaginatedResponse<PositionDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.getContent().stream().map(this::converter).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        logger.info("Paginated response built successfully");
        return response;
    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Creating pageable for sortBy={}, sortDir={}", sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PositionDto converter(PositionModel entity) {
        logger.trace("Converting PositionModel to DTO for ID: {}", entity.getPositionId());
        PositionDto dto = new PositionDto();
        dto.setPositionId(entity.getPositionId());
        dto.setPositionTitle(entity.getPositionTitle());
        dto.setPositionDescription(entity.getPositionDescription());
        dto.setPositionCriteria(entity.getPositionCriteria());
        dto.setPositionTotalOpening(entity.getPositionTotalOpening());
        dto.setPositionLocation(entity.getPositionLocation());
        dto.setPositionType(entity.getPositionType());
        dto.setPositionSalary(entity.getPositionSalary());
        dto.setPositionLanguage(entity.getPositionLanguage());
        dto.setPositionApplications(entity.getPositionApplications().size());

        dto.setCreatedById(entity.getCreatedBy().getUserId());
        dto.setCreatedByName(entity.getCreatedBy().getUsername());

        PositionStatusDto positionStatus = new PositionStatusDto();
        positionStatus.setPositionStatusId(entity.getPositionStatus().getPositionStatusId());
        positionStatus.setStatus(entity.getPositionStatus().getPositionStatus());
        positionStatus.setPositionStatusReason(entity.getPositionStatus().getPositionStatusReason());
        dto.setPositionStatus(positionStatus);

        List<PositionRequirementDto> requirements = new ArrayList<>();
        entity.getPositionRequirements().forEach(requirement -> requirements.add(convertor(requirement)));
        dto.setPositionRequirements(requirements);
        dto.setPositionRequiredEducations(entity.getPositionRequiredEducations().stream().map(this::convertor).toList());
        return dto;
    }

    private PositionRequirementDto convertor(PositionRequirementModel entity) {
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

    private DegreeDto convertor(DegreeModel entity) {
        DegreeDto dto = new DegreeDto();
        dto.setDegreeId(entity.getDegreeId());
        dto.setDegree(entity.getDegree());
        dto.setStream(entity.getStream());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
