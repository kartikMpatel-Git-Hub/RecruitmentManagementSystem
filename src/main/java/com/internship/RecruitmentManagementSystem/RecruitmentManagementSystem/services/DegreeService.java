package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ErrorResponseException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.DegreeDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DegreeModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.DegreeRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.DegreeServiceInterface;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Transactional
    public DegreeDto addDegree(DegreeDto degreeDto) {
        validateDegreeDto(degreeDto);

        logger.info("Adding new degree: {}", degreeDto.getDegree());
        DegreeModel savedDegreeModel = degreeRepository.save(convertor(degreeDto));
        logger.info("Successfully added degree with ID: {}", savedDegreeModel.getDegreeId());

        return convertor(savedDegreeModel);
    }

    @Override
    @Transactional
    public void deleteDegree(Integer degreeId) {
        validateDegreeId(degreeId);

        logger.info("Deleting degree with ID: {}", degreeId);
        findDegreeById(degreeId);
        degreeRepository.deleteById(degreeId);
        logger.info("Successfully deleted degree with ID: {}", degreeId);
    }

    @Override
    @Transactional
    public DegreeDto updateDegree(Integer degreeId, DegreeDto newDegree) {
        validateDegreeId(degreeId);

        logger.info("Updating degree with ID: {}", degreeId);
        DegreeModel degree = findDegreeById(degreeId);

        updateDegreeFields(degree, newDegree);
        DegreeModel updatedDegree = degreeRepository.save(degree);
        logger.info("Successfully updated degree with ID: {}", degreeId);

        return convertor(updatedDegree);
    }

    @Override
    public DegreeDto getDegree(Integer degreeId) {
        validateDegreeId(degreeId);
        return convertor(findDegreeById(degreeId));
    }

    @Override
    public List<DegreeDto> getAllDegrees() {
        logger.info("Fetching all degrees");
        List<DegreeModel> degreeModels = degreeRepository.findAll();
        return degreeModels.stream().map(this::convertor).toList();
    }

    private void validateDegreeDto(DegreeDto degreeDto) {
        List<String> errors = new ArrayList<>();

        if (degreeDto.getDegree() == null || degreeDto.getDegree().trim().isEmpty()) {
            errors.add(DEGREE_NOT_FOUND);
        }
        if (degreeDto.getStream() == null || degreeDto.getStream().toString().trim().isEmpty()) {
            errors.add(STREAM_NOT_FOUND);
        }

        if (!errors.isEmpty()) {
            throw new ErrorResponseException(INSUFFICIENT_DATA,
                    new ErrorResponse(400, INSUFFICIENT_DATA, errors, false));
        }
    }

    private void validateDegreeId(Integer degreeId) {
        if (degreeId == null || degreeId <= 0) {
            throw new IllegalArgumentException("Invalid degree ID");
        }
    }

    private DegreeModel findDegreeById(Integer degreeId) {
        return degreeRepository.findById(degreeId)
                .orElseThrow(() -> new ResourceNotFoundException("Degree", "ID", degreeId.toString()));
    }

    private void updateDegreeFields(DegreeModel degree, DegreeDto newDegree) {
        if (newDegree.getDegree() != null && !newDegree.getDegree().trim().isEmpty()) {
            degree.setDegree(newDegree.getDegree());
        }
        if (newDegree.getStream() != null && !newDegree.getStream().toString().trim().isEmpty()) {
            degree.setStream(newDegree.getStream());
        }
    }

    private DegreeDto convertor(DegreeModel degreeModel) {
        return modelMapper.map(degreeModel, DegreeDto.class);
    }

    private DegreeModel convertor(DegreeDto degreeDto) {
        return modelMapper.map(degreeDto, DegreeModel.class);
    }
}
