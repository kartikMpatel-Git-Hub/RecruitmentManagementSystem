package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UniversityDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UniversityModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UniversityRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.UniversityServiceInterface;
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

    private final UniversityRepository universityRepository;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(value = "universityData", allEntries = true)
    public UniversityDto addUniversity(UniversityDto university) {
        logger.info("Attempting to add new university: {}", university.getUniversity());

        if (universityRepository.existsByUniversity(university.getUniversity())) {
            logger.error("University '{}' already exists!", university.getUniversity());
            throw new ResourceAlreadyExistsException("University already exists");
        }

        UniversityModel newUniversity = universityRepository.save(convertor(university));
        logger.info("Successfully added university with ID: {}", newUniversity.getUniversityId());

        return convertor(newUniversity);
    }

    @Override
    @Cacheable(value = "userUniversity" ,key = "'universityName_' + #universityName")
    public UniversityDto getUniversityByName(String universityName) {
        logger.info("Fetching university by name: {}", universityName);

        UniversityModel universityModel = universityRepository.findByUniversity(universityName)
                .orElseThrow(() -> {
                    logger.error("University not found with name: {}", universityName);
                    return new ResourceNotFoundException("University", "universityName", universityName);
                });

        logger.info("Successfully fetched university: {}", universityName);
        return convertor(universityModel);
    }

    @Override
    @Cacheable(value = "userUniversity",key = "'id_' + #universityId")
    public UniversityDto getUniversityById(Integer universityId) {
        logger.info("Fetching university by ID: {}", universityId);

        UniversityModel universityModel = universityRepository.findById(universityId)
                .orElseThrow(() -> {
                    logger.error("University not found with ID: {}", universityId);
                    return new ResourceNotFoundException("University", "universityId", universityId.toString());
                });

        logger.info("Successfully fetched university with ID: {}", universityId);
        return convertor(universityModel);
    }

    @Override
    @Cacheable(value = "universityData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<UniversityDto> getAllUniversities(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching all universities - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UniversityModel> universitiesPages = universityRepository.findAll(pageable);

        PaginatedResponse<UniversityDto> response = new PaginatedResponse<>();
        response.setData(universitiesPages.getContent().stream().map(this::convertor).toList());
        response.setCurrentPage(universitiesPages.getNumber());
        response.setLast(universitiesPages.isLast());
        response.setPageSize(universitiesPages.getSize());
        response.setTotalItems(universitiesPages.getTotalElements());
        response.setTotalPages(universitiesPages.getTotalPages());

        logger.info("Successfully fetched {} universities", universitiesPages.getNumberOfElements());
        return response;
    }

    @Override
//    @CacheEvict(value = "universityData", allEntries = true)
    @Caching(evict = {
            @CacheEvict(value = "universityData",allEntries = true),
            @CacheEvict(value = "userUniversity",allEntries = true)
    })
    public UniversityDto updateUniversity(Integer universityId, UniversityDto universityDto) {
        logger.info("Updating university with ID: {}", universityId);

        UniversityModel existingUniversity = universityRepository.findById(universityId)
                .orElseThrow(() -> {
                    logger.error("University not found with ID: {}", universityId);
                    return new ResourceNotFoundException("University", "universityId", universityId.toString());
                });

        if (universityDto.getUniversity() != null && !universityDto.getUniversity().trim().isEmpty()) {
            logger.debug("Updating university name from '{}' to '{}'",
                    existingUniversity.getUniversity(), universityDto.getUniversity());
            existingUniversity.setUniversity(universityDto.getUniversity());
        }

        UniversityModel updatedUniversity = universityRepository.save(existingUniversity);
        logger.info("Successfully updated university with ID: {}", universityId);

        return convertor(updatedUniversity);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "universityData",allEntries = true),
            @CacheEvict(value = "userUniversity",allEntries = true)
    })
    public void deleteUniversity(Integer universityId) {
        logger.info("Deleting university with ID: {}", universityId);

        UniversityModel existingUniversity = universityRepository.findById(universityId)
                .orElseThrow(() -> {
                    logger.error("University not found with ID: {}", universityId);
                    return new ResourceNotFoundException("University", "universityId", universityId.toString());
                });

        universityRepository.delete(existingUniversity);
        logger.info("Successfully deleted university with ID: {}", universityId);
    }

    private UniversityDto convertor(UniversityModel universityModel) {
        return modelMapper.map(universityModel, UniversityDto.class);
    }

    private UniversityModel convertor(UniversityDto universityDto) {
        return modelMapper.map(universityDto, UniversityModel.class);
    }
}
