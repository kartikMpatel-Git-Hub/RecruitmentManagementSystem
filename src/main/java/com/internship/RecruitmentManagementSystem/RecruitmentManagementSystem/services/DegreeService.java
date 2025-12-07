package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ErrorResponseException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.degree.DegreeResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DegreeModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.DegreeRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.DegreeServiceInterface;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DegreeService implements DegreeServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(DegreeService.class);
    private static final String DEGREE_NOT_FOUND = "Degree Not Found!";
    private static final String STREAM_NOT_FOUND = "Stream Not Found!";
    private static final String INSUFFICIENT_DATA = "Insufficient Data";

    private final DegreeRepository degreeRepository;
    private final ModelMapper modelMapper;

    public DegreeService(DegreeRepository degreeRepository, ModelMapper modelMapper) {
        this.degreeRepository = degreeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @CacheEvict(value = "degreeData", allEntries = true)
    public DegreeResponseDto addDegree(DegreeCreateDto degreeDto) {
        logger.info("Attempting to add new degree: {}", degreeDto.getDegree());

        DegreeModel savedDegreeModel = degreeRepository.save(convertor(degreeDto));
        logger.info("Successfully added degree with ID: {} and Name: {}", savedDegreeModel.getDegreeId(), savedDegreeModel.getDegree());
        return convertor(savedDegreeModel);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "degreeData", allEntries = true),
            @CacheEvict(value = "userData", key = "'id_' + #degreeId")
    })
    public void deleteDegree(Integer degreeId) {
        logger.info("Attempting to delete degree with ID: {}", degreeId);
        validateDegreeId(degreeId);

        DegreeModel degree = findDegreeById(degreeId);
        degreeRepository.delete(degree);
        logger.info("Successfully deleted degree with ID: {} and Name: {}", degreeId, degree.getDegree());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "degreeData", allEntries = true),
            @CacheEvict(value = "userData", key = "'id_' + #degreeId")
    })
    public DegreeResponseDto updateDegree(Integer degreeId, DegreeUpdateDto newDegree) {
        logger.info("Attempting to update degree with ID: {}", degreeId);
        validateDegreeId(degreeId);

        DegreeModel degree = findDegreeById(degreeId);
        updateDegreeFields(degree, newDegree);

        DegreeModel updatedDegree = degreeRepository.save(degree);
        logger.info("Successfully updated degree with ID: {}. New Degree Name: {}", degreeId, updatedDegree.getDegree());
        return convertor(updatedDegree);
    }

    @Override
    @Cacheable(value = "userDegree",key = "'id_' + #degreeId")
    public DegreeResponseDto getDegree(Integer degreeId) {
        logger.info("Fetching degree with ID: {}", degreeId);
        validateDegreeId(degreeId);

        DegreeModel degree = findDegreeById(degreeId);
        logger.info("Successfully fetched degree with ID: {} and Name: {}", degreeId, degree.getDegree());
        return convertor(degree);
    }

    @Override
    @Cacheable(value = "degreeData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<DegreeResponseDto> getAllDegrees(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching all degrees - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DegreeModel> pageResponse = degreeRepository.findAll(pageable);

        PaginatedResponse<DegreeResponseDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.stream().map(this::convertor).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());

        logger.info("Fetched {} degrees (Page {}/{})", response.getData().size(), response.getCurrentPage() + 1, response.getTotalPages());
        return response;
    }

    private void validateDegreeDto(DegreeResponseDto degreeDto) {
        List<String> errors = new ArrayList<>();
        if (degreeDto.getDegree() == null || degreeDto.getDegree().trim().isEmpty()) {
            errors.add(DEGREE_NOT_FOUND);
            logger.error("Degree name missing");
        }
        if (degreeDto.getStream() == null || degreeDto.getStream().toString().trim().isEmpty()) {
            errors.add(STREAM_NOT_FOUND);
            logger.error("Stream missing");
        }

        if (!errors.isEmpty()) {
            logger.error("Degree DTO validation failed: {}", errors);
            throw new ErrorResponseException(INSUFFICIENT_DATA, new ErrorResponse(400, INSUFFICIENT_DATA, errors, false));
        }

        logger.debug("Degree DTO validated successfully: {}", degreeDto);
    }

    private void validateDegreeId(Integer degreeId) {
        if (degreeId == null || degreeId <= 0) {
            logger.error("Invalid degree ID: {}", degreeId);
            throw new IllegalArgumentException("Invalid degree ID");
        }
        logger.debug("Degree ID validated: {}", degreeId);
    }

    private DegreeModel findDegreeById(Integer degreeId) {
        return degreeRepository.findById(degreeId)
                .orElseThrow(() -> {
                    logger.error("Degree not found with ID: {}", degreeId);
                    return new ResourceNotFoundException("Degree", "ID", degreeId.toString());
                });
    }

    private void updateDegreeFields(DegreeModel degree, DegreeUpdateDto newDegree) {
        if (newDegree.getDegree() != null && !newDegree.getDegree().trim().isEmpty()) {
            logger.debug("Updating degree name from '{}' to '{}'", degree.getDegree(), newDegree.getDegree());
            degree.setDegree(newDegree.getDegree());
        }
        if (newDegree.getStream() != null && !newDegree.getStream().toString().trim().isEmpty()) {
            logger.debug("Updating degree stream from '{}' to '{}'", degree.getStream(), newDegree.getStream());
            degree.setStream(newDegree.getStream());
        }
    }

    private DegreeResponseDto convertor(DegreeModel degreeModel) {
        return modelMapper.map(degreeModel, DegreeResponseDto.class);
    }

    private DegreeModel convertor(DegreeCreateDto degreeModel) {
        return modelMapper.map(degreeModel, DegreeModel.class);
    }

}
