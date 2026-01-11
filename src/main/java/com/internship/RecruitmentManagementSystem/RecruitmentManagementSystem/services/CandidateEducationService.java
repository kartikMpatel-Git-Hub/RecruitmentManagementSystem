package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.education.CandidateEducationCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.education.CandidateEducationUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateEducationResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateEducationServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateEducationService implements CandidateEducationServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CandidateEducationService.class);
    private final PaginatedResponseCreator paginatedResponseCreator;
    private final ModelServiceInterface modelService;
    private final Mapper mapper;


    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateEducationData",allEntries = true)
    })
    public CandidateEducationResponseDto addCandidateEducation(CandidateEducationCreateDto candidateEducationDto) {
        logger.info("Adding candidate education for candidateId: {}, degreeId: {}, universityId: {}",
                candidateEducationDto.getCandidate(), candidateEducationDto.getDegree(), candidateEducationDto.getUniversity());

        DegreeModel degree = modelService.getDegree(candidateEducationDto.getDegree());

        CandidateModel candidate = modelService.getCandidate(candidateEducationDto.getCandidate());

        UniversityModel university = modelService.getUniversity(candidateEducationDto.getUniversity());

        CandidateEducationModel candidateEducationModel = new CandidateEducationModel();
        candidateEducationModel.setDegree(degree);
        candidateEducationModel.setCandidate(candidate);
        candidateEducationModel.setUniversity(university);
        candidateEducationModel.setPercentage(candidateEducationDto.getPercentage());
        candidateEducationModel.setPassingYear(candidateEducationDto.getPassingYear());

        CandidateEducationModel newCandidateEducation = modelService.addCandidateEducation(candidateEducationModel);
        logger.info("Successfully added candidate education with ID: {}", newCandidateEducation.getCandidateEducationId());
        return mapper.toDto(candidateEducationModel, CandidateEducationResponseDto.class);
    }

    @Override
    @Cacheable(value = "candidateEducationData" ,key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationResponseDto> getAllCandidateEducations(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching all candidate educations - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        Page<CandidateEducationModel> pageData = modelService.getAllCandidateEducations(pageable);
        PaginatedResponse<CandidateEducationResponseDto> response = paginatedResponseCreator.getPaginatedResponse(
                pageData, CandidateEducationResponseDto.class);
        logger.info("Fetched {} candidate education records (Page {}/{})", response.getData().size(), page + 1, response.getTotalPages());
        return response;
    }


    @Override
    @Cacheable(value = "candidateEducationData",key = "'educationId_'+#candidateEducationId")
    public CandidateEducationResponseDto getCandidateEducationById(Integer candidateEducationId) {
        logger.info("Fetching candidate education by ID: {}", candidateEducationId);
        CandidateEducationModel candidateEducation = modelService.getCandidateEducation(candidateEducationId);
        return mapper.toDto(candidateEducation, CandidateEducationResponseDto.class);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateEducationData",allEntries = true),
    })
    public CandidateEducationResponseDto updateCandidateEducation(Integer candidateEducationId, CandidateEducationUpdateDto candidateEducationDto) {
        logger.info("Updating candidate education with ID: {}", candidateEducationId);

        CandidateEducationModel existingCandidateEducation = modelService.getCandidateEducation(candidateEducationId);

        if (candidateEducationDto.getDegree() != null) {
            DegreeModel newDegree = modelService.getDegree(candidateEducationDto.getDegree());
            existingCandidateEducation.setDegree(newDegree);
        }
        if (candidateEducationDto.getUniversity() != null) {
            UniversityModel newUniversity = modelService.getUniversity(candidateEducationDto.getUniversity());
            existingCandidateEducation.setUniversity(newUniversity);
        }
        if (candidateEducationDto.getPercentage() != null) {
            existingCandidateEducation.setPercentage(candidateEducationDto.getPercentage());
        }
        if (candidateEducationDto.getPassingYear() != null) {
            existingCandidateEducation.setPassingYear(candidateEducationDto.getPassingYear());
        }

        CandidateEducationModel updatedCandidateEducation = modelService.addCandidateEducation(existingCandidateEducation);
        logger.info("Updated candidate education successfully with ID: {}", candidateEducationId);
        return mapper.toDto(updatedCandidateEducation, CandidateEducationResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateEducationData",allEntries = true),
    })
    public void deleteCandidateEducation(Integer candidateEducationId) {
        logger.info("Deleting candidate education with ID: {}", candidateEducationId);
        CandidateEducationModel existingCandidateEducation = modelService.getCandidateEducation(candidateEducationId);
        modelService.removeCandidateEducation(existingCandidateEducation);
        logger.info("Deleted candidate education successfully with ID: {}", candidateEducationId);
    }

    @Override
    @Cacheable(value = "candidateEducationData",key = "'universityId_'+#universityId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationResponseDto> getCandidateEducationByUniversityId(Integer universityId, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidate educations for universityId: {}, Page: {}, Size: {}", universityId, page, size);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        Page<CandidateEducationModel> pageData = modelService.getAllCandidateEducationByUniversity(universityId,pageable);
        PaginatedResponse<CandidateEducationResponseDto> response = paginatedResponseCreator.getPaginatedResponse(
                pageData, CandidateEducationResponseDto.class);
        logger.info("Fetched {} candidate education with university id : {} records (Page {}/{})", response.getData().size(), universityId, page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Cacheable(value = "candidateEducationData",key = "'candidateId_'+#candidateId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationResponseDto> getCandidateEducationByCandidateId(Integer candidateId, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidate educations for candidateId: {}, Page: {}, Size: {}", candidateId, page, size);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        Page<CandidateEducationModel> pageData = modelService.getAllCandidateEducationByCandidate(candidateId, pageable);
        PaginatedResponse<CandidateEducationResponseDto> response = paginatedResponseCreator.getPaginatedResponse(
                pageData, CandidateEducationResponseDto.class);
        logger.info("Fetched {} candidate education by candidate id : {} records (Page {}/{})", response.getData().size(), candidateId, page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Cacheable(value = "candidateEducationData",key = "'degreeId_'+#degreeId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationResponseDto> getCandidateEducationByDegreeId(Integer degreeId, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidate educations for degreeId: {}, Page: {}, Size: {}", degreeId, page, size);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        Page<CandidateEducationModel> pageData = modelService.getAllCandidateEducationByDegree(degreeId, pageable);
        PaginatedResponse<CandidateEducationResponseDto> response = paginatedResponseCreator.getPaginatedResponse(
                pageData, CandidateEducationResponseDto.class);
        logger.info("Fetched {} candidate education by degree id : {} records (Page {}/{})", response.getData().size(), degreeId, page + 1, response.getTotalPages());
        return response;
    }
}
