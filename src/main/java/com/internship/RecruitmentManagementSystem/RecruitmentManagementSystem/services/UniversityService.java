package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.university.UniversityCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.university.UniversityUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.university.UniversityResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UniversityModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UniversityRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.UniversityServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
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

@RequiredArgsConstructor
@Service
public class UniversityService implements UniversityServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(UniversityService.class);
    private final Mapper mapper;
    private final PaginatedResponseCreator paginatedResponseCreator;
    private final ModelServiceInterface modelService;

    @Override
    @CacheEvict(value = "universityData", allEntries = true)
    public UniversityResponseDto addUniversity(UniversityCreateDto university) {
        logger.info("Attempting to add new university: {}", university.getUniversity());
        if (modelService.existsByUniversity(university.getUniversity())) {
            logger.error("University '{}' already exists!", university.getUniversity());
            throw new ResourceAlreadyExistsException("University already exists");
        }
        UniversityModel entity = mapper.toEntity(university, UniversityModel.class);
        UniversityModel newUniversity = modelService.addUniversity(entity);
        logger.info("Successfully added university with ID: {}", newUniversity.getUniversityId());
        return mapper.toDto(newUniversity, UniversityResponseDto.class);
    }

    @Override
    @Cacheable(value = "userUniversity" ,key = "'universityName_' + #universityName")
    public UniversityResponseDto getUniversityByName(String universityName) {
        logger.info("Fetching university by name: {}", universityName);
        UniversityModel universityModel = modelService.getUniversity(universityName);
        logger.info("Successfully fetched university: {}", universityName);
        return mapper.toDto(universityModel, UniversityResponseDto.class);
    }

    @Override
    @Cacheable(value = "userUniversity",key = "'id_' + #universityId")
    public UniversityResponseDto getUniversityById(Integer universityId) {
        logger.info("Fetching university by ID: {}", universityId);
        UniversityModel universityModel = modelService.getUniversity(universityId);
        logger.info("Successfully fetched university with ID: {}", universityId);
        return mapper.toDto(universityModel, UniversityResponseDto.class);
    }

    @Override
    @Cacheable(value = "universityData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<UniversityResponseDto> getAllUniversities(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching all universities - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page,size,sortBy,sortDir);
        Page<UniversityModel> universitiesPages = modelService.findAllUniversity(pageable);
        logger.info("Successfully fetched {} universities", universitiesPages.getNumberOfElements());
        return paginatedResponseCreator.getPaginatedResponse(universitiesPages, UniversityResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "universityData",allEntries = true),
            @CacheEvict(value = "userUniversity",allEntries = true)
    })
    public UniversityResponseDto updateUniversity(Integer universityId, UniversityUpdateDto universityDto) {
        logger.info("Updating university with ID: {}", universityId);

        UniversityModel existingUniversity = modelService.getUniversity(universityId);

        if (universityDto.getUniversity() != null && !universityDto.getUniversity().trim().isEmpty()) {
            logger.debug("Updating university name from '{}' to '{}'",
                    existingUniversity.getUniversity(), universityDto.getUniversity());
            existingUniversity.setUniversity(universityDto.getUniversity());
        }
        UniversityModel updatedUniversity = modelService.addUniversity(existingUniversity);
        logger.info("Successfully updated university with ID: {}", universityId);
        return mapper.toDto(updatedUniversity, UniversityResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "universityData",allEntries = true),
            @CacheEvict(value = "userUniversity",allEntries = true)
    })
    public void deleteUniversity(Integer universityId) {
        logger.info("Deleting university with ID: {}", universityId);

        UniversityModel existingUniversity = modelService.getUniversity(universityId);
        modelService.removeUniversity(existingUniversity);
        logger.info("Successfully deleted university with ID: {}", universityId);
    }
}
