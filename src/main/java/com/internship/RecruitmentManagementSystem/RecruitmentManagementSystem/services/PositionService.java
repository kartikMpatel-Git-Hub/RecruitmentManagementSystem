package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeGetDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.position.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.degree.DegreeResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.position.PositionRequirementResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.position.PositionResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.position.PositionRoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.position.PositionStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
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
    private final PositionRoundRepository positionRoundRepository;
    private final DegreeRepository degreeRepository;

    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public PositionResponseDto addPosition(PositionCreateDto newPosition) {
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
        position.setPositionMinYearsOfExperience(newPosition.getPositionMinYearsOfExperience());
        position.setPositionSalary(newPosition.getPositionSalary());
        position.setPositionStatus(positionStatus);
        position.setPositionLanguage(newPosition.getPositionLanguage());

        PositionModel savedPosition = positionRepository.save(position);

        List<PositionRoundModel> positionRound = new ArrayList<>();
        newPosition.getPositionRounds().forEach(round -> {
            PositionRoundModel newRound = new PositionRoundModel();
            newRound.setPosition(savedPosition);
            newRound.setPositionRoundType(round.getPositionRoundType());
            newRound.setPositionRoundSequence(round.getPositionRoundSequence());
            positionRound.add(positionRoundRepository.save(newRound));
        });
        position.setPositionRounds(positionRound);
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
        position.setPositionRequirements(requirements);

        logger.debug("Position requirements added successfully for position ID: {}", savedPosition.getPositionId());

        List<DegreeModel> education = newPosition.getPositionRequiredEducations().stream().map(
                degree -> degreeRepository.findById(degree.getDegreeId()).orElseThrow(() -> {
                    logger.error("Degree not found : {}", degree.getDegreeId());
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
    public PositionResponseDto updatePosition(Integer positionId, PositionUpdateDto newPosition) {
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
        if(newPosition.getPositionMinYearsOfExperience() != null) oldPosition.setPositionMinYearsOfExperience(newPosition.getPositionMinYearsOfExperience());
        if (newPosition.getPositionSalary() != null) oldPosition.setPositionSalary(newPosition.getPositionSalary());
        if (newPosition.getPositionType() != null) oldPosition.setPositionType(newPosition.getPositionType());
        if (newPosition.getPositionLanguage() != null) oldPosition.setPositionLanguage(newPosition.getPositionLanguage());

        PositionModel updatedPosition = positionRepository.save(oldPosition);
        logger.info("Position updated successfully for ID: {}", positionId);
        return converter(updatedPosition);
    }

    @Override
    @Cacheable(value = "positionData", key = "'positionId_'+#positionId")
    public PositionResponseDto getPosition(Integer positionId) {
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
    public PositionResponseDto changeEducation(Integer positionId, List<DegreeGetDto> positionEducation) {
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
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public PositionRoundResponseDto changeRound(Integer positionId, PositionRoundUpdateDto positionRound) {

        PositionRoundModel existingPositionRound = positionRoundRepository.findById(positionId).orElseThrow(
                ()->new ResourceNotFoundException("PositionRound","positionRoundId",positionId.toString())
        );

        if(positionRound.getPositionRoundType() != null){
            existingPositionRound.setPositionRoundType(positionRound.getPositionRoundType());
            existingPositionRound.setPositionRoundSequence(positionRound.getPositionRoundSequence());
        }

        return convertor(positionRoundRepository.save(existingPositionRound));
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public void deleteRound(Integer positionId) {

        PositionRoundModel existingPositionRound = positionRoundRepository.findById(positionId).orElseThrow(
                ()->new ResourceNotFoundException("PositionRound","positionRoundId",positionId.toString())
        );
        positionRoundRepository.delete(existingPositionRound);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "positionData",allEntries = true)
    })
    public PositionRequirementResponseDto addPositionRequirement(Integer positionId, PositionRequirementCreateDto newRequirement) {
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
    public PositionRequirementResponseDto updatePositionRequirement(Integer positionRequirementId, PositionRequirementUpdateDto newPosition) {
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
    public PaginatedResponse<PositionRequirementResponseDto> getPositionRequirements(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching position requirements for positionId: {}, page: {}, size: {}, sortBy: {}, sortDir: {}", positionId, page, size, sortBy, sortDir);
        PaginatedResponse<PositionRequirementResponseDto> response = getPaginatedPositionRequirement(positionRequirementRepository.findAll(getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} position requirements", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "positionData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<PositionResponseDto> getAllPositions(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching all positions page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        return getPaginatedPosition(positionRepository.findAllWithUser(getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Cacheable(value = "positionData", key = "'recruiterId_'+#recruiterId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<PositionResponseDto> getAllPositionsByRecruiter(Integer recruiterId,Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching all positions By Recruiter page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        return getPaginatedPosition(positionRepository.findPositionByRecruiter(recruiterId,getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Cacheable(value = "positionData", key = "'position_count'")
    public Long countActivePosition() {
        logger.debug("Counting all active positions");
        Long count = positionRepository.countActivePosition();
        logger.info("Total active positions: {}", count);
        return count;
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "positionData", allEntries = true)})
    public PositionResponseDto addRound(Integer positionId, PositionRoundCreateDto positionRound) {
        PositionModel existingPosition = positionRepository.findById(positionId).orElseThrow(
                ()->new ResourceNotFoundException("Position","positionId",positionId.toString())
        );

        PositionRoundModel newPositionRound = new PositionRoundModel();
        newPositionRound.setPositionRoundType(positionRound.getPositionRoundType());
        newPositionRound.setPositionRoundSequence(positionRound.getPositionRoundSequence());
        newPositionRound.setPosition(existingPosition);

        existingPosition.getPositionRounds().add(positionRoundRepository.save(newPositionRound));

        return converter(existingPosition);
    }

    private PaginatedResponse<PositionRequirementResponseDto> getPaginatedPositionRequirement(Page<PositionRequirementModel> pageResponse) {
        PaginatedResponse<PositionRequirementResponseDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.getContent().stream().map(this::convertor).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        return response;
    }

    private PaginatedResponse<PositionResponseDto> getPaginatedPosition(Page<PositionModel> pageResponse) {
        logger.debug("Building paginated response for {} positions", pageResponse.getContent().size());
        PaginatedResponse<PositionResponseDto> response = new PaginatedResponse<>();
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

    private PositionResponseDto converter(PositionModel entity) {
        logger.trace("Converting PositionModel to DTO for ID: {}", entity.getPositionId());
        PositionResponseDto dto = new PositionResponseDto();
        dto.setPositionId(entity.getPositionId());
        dto.setPositionTitle(entity.getPositionTitle());
        dto.setPositionDescription(entity.getPositionDescription());
        dto.setPositionCriteria(entity.getPositionCriteria());
        dto.setPositionTotalOpening(entity.getPositionTotalOpening());
        dto.setPositionLocation(entity.getPositionLocation());
        dto.setPositionType(entity.getPositionType());
        dto.setPositionMinYearsOfExperience(entity.getPositionMinYearsOfExperience());
        dto.setPositionSalary(entity.getPositionSalary());
        dto.setPositionLanguage(entity.getPositionLanguage());
        int applications = 0;
        if(entity.getPositionApplications() != null) {
            applications = entity.getPositionApplications().size();
        }
        dto.setPositionApplications(applications);

        dto.setCreatedBy(converter(entity.getCreatedBy()));


        dto.setPositionStatus(getPositionStatus(entity.getPositionStatus()));

        dto.setPositionRequirements(entity.getPositionRequirements() != null ? entity.getPositionRequirements().stream().map(this::convertor).toList() : null);
        dto.setPositionRequiredEducations(entity.getPositionRequiredEducations().stream().map(this::convertor).toList());
        dto.setPositionRounds(entity.getPositionRounds().stream().map(this::convertor).toList());

        return dto;
    }
    private PositionStatusResponseDto getPositionStatus(PositionStatusModel entity){
        PositionStatusResponseDto positionStatus = new PositionStatusResponseDto();
        positionStatus.setPositionStatusId(entity.getPositionStatusId());
        positionStatus.setStatus(entity.getPositionStatus());
        positionStatus.setPositionStatusReason(entity.getPositionStatusReason());
        return positionStatus;
    }
    private UserResponseDto converter(UserModel entity) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(entity.getUserId());
        dto.setUserName(entity.getUsername());
        dto.setUserEmail(entity.getUserEmail());
        dto.setUserImageUrl(entity.getUserImageUrl());
        return dto;
    }

    private PositionRequirementResponseDto convertor(PositionRequirementModel entity) {
        PositionRequirementResponseDto dto = new PositionRequirementResponseDto();
        dto.setPositionRequirementId(entity.getPositionRequirementId());
        dto.setPosition(entity.getPosition().getPositionId());

        SkillResponseDto skill = new SkillResponseDto();
        skill.setSkillId(entity.getPositionRequiredSkill().getSkillId());
        skill.setSkill(entity.getPositionRequiredSkill().getSkill());
        dto.setMinYearsOfExperience(entity.getMinYearsOfExperience());
        dto.setPositionSkill(skill);
        dto.setPositionRequirement(entity.getPositionRequirement());
        return dto;
    }

    private DegreeResponseDto convertor(DegreeModel entity) {
        DegreeResponseDto dto = new DegreeResponseDto();
        dto.setDegreeId(entity.getDegreeId());
        dto.setDegree(entity.getDegree());
        dto.setStream(entity.getStream());
        return dto;
    }

    private PositionRoundResponseDto convertor(PositionRoundModel entity){
        PositionRoundResponseDto dto = new PositionRoundResponseDto();

        dto.setPositionRoundId(entity.getPositionRoundId());
        dto.setPositionRoundType(entity.getPositionRoundType());
        dto.setPositionRoundSequence(entity.getPositionRoundSequence());

        return dto;
    }
}
