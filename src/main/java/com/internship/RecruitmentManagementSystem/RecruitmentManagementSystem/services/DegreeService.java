package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.degree.DegreeResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DegreeModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.DegreeServiceInterface;
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
public class DegreeService implements DegreeServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(DegreeService.class);

    private final ModelServiceInterface modelService;
    private final Mapper mapper;
    private final PaginatedResponseCreator paginatedResponseCreator;

    @Override
    @CacheEvict(value = "degreeData", allEntries = true)
    public DegreeResponseDto addDegree(DegreeCreateDto degreeDto) {
        logger.info("Attempting to add new degree: {}", degreeDto.getDegree());
        DegreeModel entity = mapper.toEntity(degreeDto, DegreeModel.class);
        DegreeModel savedDegreeModel = modelService.addDegree(entity);
        logger.info("Successfully added degree with ID: {} and Name: {}", savedDegreeModel.getDegreeId(), savedDegreeModel.getDegree());
        return mapper.toDto(savedDegreeModel, DegreeResponseDto.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "degreeData", allEntries = true),
            @CacheEvict(value = "userData", key = "'id_' + #degreeId")
    })
    public void deleteDegree(Integer degreeId) {
        logger.info("Attempting to delete degree with ID: {}", degreeId);
        DegreeModel degree = modelService.getDegree(degreeId);
        modelService.removeDegree(degree);
        logger.info("Successfully deleted degree with ID: {} and Name: {}", degreeId, degree.getDegree());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "degreeData", allEntries = true),
            @CacheEvict(value = "userData", key = "'id_' + #degreeId")
    })
    public DegreeResponseDto updateDegree(Integer degreeId, DegreeUpdateDto newDegree) {
        logger.info("Attempting to update degree with ID: {}", degreeId);
        DegreeModel degree = modelService.getDegree(degreeId);
        updateDegreeFields(degree, newDegree);
        DegreeModel updatedDegree = modelService.addDegree(degree);
        logger.info("Successfully updated degree with ID: {}. New Degree Name: {}", degreeId, updatedDegree.getDegree());
        return mapper.toDto(updatedDegree, DegreeResponseDto.class);
    }

    @Override
    @Cacheable(value = "userDegree",key = "'id_' + #degreeId")
    public DegreeResponseDto getDegree(Integer degreeId) {
        logger.info("Fetching degree with ID: {}", degreeId);
        DegreeModel degree = modelService.getDegree(degreeId);
        logger.info("Successfully fetched degree with ID: {} and Name: {}", degreeId, degree.getDegree());
        return mapper.toDto(degree, DegreeResponseDto.class);
    }

    @Override
    @Cacheable(value = "degreeData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<DegreeResponseDto> getAllDegrees(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching all degrees - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<DegreeResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getAllDegrees(pageable)
                        ,DegreeResponseDto.class
                );
        logger.info("Fetched {} degrees (Page {}/{})", response.getData().size(), response.getCurrentPage() + 1, response.getTotalPages());
        return response;
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

}
