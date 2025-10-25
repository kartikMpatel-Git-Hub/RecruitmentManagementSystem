package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateEducationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateEducationServiceInterface;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateEducationService implements CandidateEducationServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CandidateEducationService.class);

    private final CandidateEducationRepository candidateEducationRepository;
    private final DegreeRepository degreeRepository;
    private final CandidateRepository candidateRepository;
    private final UniversityRepository universityRepository;

    public CandidateEducationService(CandidateEducationRepository candidateEducationRepository,
                                     DegreeRepository degreeRepository,
                                     CandidateRepository candidateRepository,
                                     UniversityRepository universityRepository) {
        this.candidateEducationRepository = candidateEducationRepository;
        this.degreeRepository = degreeRepository;
        this.candidateRepository = candidateRepository;
        this.universityRepository = universityRepository;
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateEducationData",allEntries = true)
    })
    public CandidateEducationDto addCandidateEducation(CandidateEducationDto candidateEducationDto) {
        logger.info("Adding candidate education for candidateId: {}, degreeId: {}, universityId: {}",
                candidateEducationDto.getCandidate(), candidateEducationDto.getDegree(), candidateEducationDto.getUniversity());

        DegreeModel degree = degreeRepository.findById(candidateEducationDto.getDegree())
                .orElseThrow(() -> {
                    logger.error("Degree not found with ID: {}", candidateEducationDto.getDegree());
                    return new ResourceNotFoundException("Degree", "degreeId", candidateEducationDto.getDegree().toString());
                });

        CandidateModel candidate = candidateRepository.findById(candidateEducationDto.getCandidate())
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", candidateEducationDto.getCandidate());
                    return new ResourceNotFoundException("Candidate", "candidateId", candidateEducationDto.getCandidate().toString());
                });

        UniversityModel university = universityRepository.findById(candidateEducationDto.getUniversity())
                .orElseThrow(() -> {
                    logger.error("University not found with ID: {}", candidateEducationDto.getUniversity());
                    return new ResourceNotFoundException("University", "universityId", candidateEducationDto.getUniversity().toString());
                });

        CandidateEducationModel candidateEducationModel = new CandidateEducationModel();
        candidateEducationModel.setDegree(degree);
        candidateEducationModel.setCandidate(candidate);
        candidateEducationModel.setUniversity(university);
        candidateEducationModel.setPercentage(candidateEducationDto.getPercentage());
        candidateEducationModel.setPassingYear(candidateEducationDto.getPassingYear());

        CandidateEducationModel newCandidateEducation = candidateEducationRepository.save(candidateEducationModel);
        logger.info("Successfully added candidate education with ID: {}", newCandidateEducation.getCandidateEducationId());
        return convertor(newCandidateEducation);
    }

    @Override
    @Cacheable(value = "candidateEducationData" ,key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationDto> getAllCandidateEducations(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching all candidate educations - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
        Page<CandidateEducationModel> pageData = candidateEducationRepository.findAll(PageRequest.of(page, size, getSort(sortBy, sortDir)));
        PaginatedResponse<CandidateEducationDto> response = paginatedResponse(pageData);
        logger.info("Fetched {} candidate education records (Page {}/{})", response.getData().size(), page + 1, response.getTotalPages());
        return response;
    }

    @Override
    @Cacheable(value = "candidateEducationData",key = "'educationId_'+#candidateEducationId")
    public Optional<CandidateEducationDto> getCandidateEducationById(Integer candidateEducationId) {
        logger.info("Fetching candidate education by ID: {}", candidateEducationId);
        return candidateEducationRepository.findById(candidateEducationId).map(this::convertor);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateEducationData",allEntries = true),
    })
    public CandidateEducationDto updateCandidateEducation(Integer candidateEducationId, CandidateEducationDto candidateEducationDto) {
        logger.info("Updating candidate education with ID: {}", candidateEducationId);

        CandidateEducationModel existingCandidateEducation = candidateEducationRepository.findById(candidateEducationId)
                .orElseThrow(() -> {
                    logger.error("CandidateEducation not found with ID: {}", candidateEducationId);
                    return new ResourceNotFoundException("CandidateEducation", "candidateEducationId", candidateEducationId.toString());
                });

        if (candidateEducationDto.getCandidate() != null) {
            CandidateModel newCandidate = candidateRepository.findById(candidateEducationDto.getCandidate())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate", "candidateId", candidateEducationDto.getCandidate().toString()));
            existingCandidateEducation.setCandidate(newCandidate);
        }
        if (candidateEducationDto.getDegree() != null) {
            DegreeModel newDegree = degreeRepository.findById(candidateEducationDto.getDegree())
                    .orElseThrow(() -> new ResourceNotFoundException("Degree", "degreeId", candidateEducationDto.getDegree().toString()));
            existingCandidateEducation.setDegree(newDegree);
        }
        if (candidateEducationDto.getUniversity() != null) {
            UniversityModel newUniversity = universityRepository.findById(candidateEducationDto.getUniversity())
                    .orElseThrow(() -> new ResourceNotFoundException("University", "universityId", candidateEducationDto.getUniversity().toString()));
            existingCandidateEducation.setUniversity(newUniversity);
        }
        if (candidateEducationDto.getPercentage() != null) {
            existingCandidateEducation.setPercentage(candidateEducationDto.getPercentage());
        }
        if (candidateEducationDto.getPassingYear() != null) {
            existingCandidateEducation.setPassingYear(candidateEducationDto.getPassingYear());
        }

        CandidateEducationModel updatedCandidateEducation = candidateEducationRepository.save(existingCandidateEducation);
        logger.info("Updated candidate education successfully with ID: {}", candidateEducationId);
        return convertor(updatedCandidateEducation);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateEducationData",allEntries = true),
    })
    public void deleteCandidateEducation(Integer candidateEducationId) {
        logger.info("Deleting candidate education with ID: {}", candidateEducationId);
        CandidateEducationModel existingCandidateEducation = candidateEducationRepository.findById(candidateEducationId)
                .orElseThrow(() -> {
                    logger.error("CandidateEducation not found with ID: {}", candidateEducationId);
                    return new ResourceNotFoundException("CandidateEducation", "candidateEducationId", candidateEducationId.toString());
                });
        candidateEducationRepository.delete(existingCandidateEducation);
        logger.info("Deleted candidate education successfully with ID: {}", candidateEducationId);
    }

    @Override
    @Cacheable(value = "candidateEducationData",key = "'universityId_'+#universityId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationDto> getCandidateEducationByUniversityId(Integer universityId, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidate educations for universityId: {}, Page: {}, Size: {}", universityId, page, size);
        Page<CandidateEducationModel> pageData = candidateEducationRepository.findByUniversity_UniversityId(universityId, PageRequest.of(page, size, getSort(sortBy, sortDir)));
        return paginatedResponse(pageData);
    }

    @Override
    @Cacheable(value = "candidateEducationData",key = "'candidateId_'+#candidateId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationDto> getCandidateEducationByCandidateId(Integer candidateId, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidate educations for candidateId: {}, Page: {}, Size: {}", candidateId, page, size);
        Page<CandidateEducationModel> pageData = candidateEducationRepository.findByCandidate_CandidateId(candidateId, PageRequest.of(page, size, getSort(sortBy, sortDir)));
        return paginatedResponse(pageData);
    }

    @Override
    @Cacheable(value = "candidateEducationData",key = "'degreeId_'+#degreeId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateEducationDto> getCandidateEducationByDegreeId(Integer degreeId, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidate educations for degreeId: {}, Page: {}, Size: {}", degreeId, page, size);
        Page<CandidateEducationModel> pageData = candidateEducationRepository.findByDegree_DegreeId(degreeId, PageRequest.of(page, size, getSort(sortBy, sortDir)));
        return paginatedResponse(pageData);
    }

    private Sort getSort(String sortBy, String sortDir) {
        return sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }

    private CandidateEducationDto convertor(CandidateEducationModel entity) {
        CandidateEducationDto dto = new CandidateEducationDto();
        dto.setCandidateEducationId(entity.getCandidateEducationId());
        dto.setCandidate(entity.getCandidate().getCandidateId());
        dto.setCandidateName(entity.getCandidate().getCandidateFirstName());
        dto.setDegree(entity.getDegree().getDegreeId());
        dto.setDegreeName(entity.getDegree().getDegree());
        dto.setUniversity(entity.getUniversity().getUniversityId());
        dto.setUniversityName(entity.getUniversity().getUniversity());
        dto.setPercentage(entity.getPercentage());
        dto.setPassingYear(entity.getPassingYear());
        return dto;
    }

    private PaginatedResponse<CandidateEducationDto> paginatedResponse(Page<CandidateEducationModel> pageData) {
        PaginatedResponse<CandidateEducationDto> response = new PaginatedResponse<>();
        response.setData(pageData.getContent().stream().map(this::convertor).toList());
        response.setCurrentPage(pageData.getNumber());
        response.setPageSize(pageData.getSize());
        response.setTotalItems(pageData.getTotalElements());
        response.setTotalPages(pageData.getTotalPages());
        response.setLast(pageData.isLast());
        return response;
    }
}
