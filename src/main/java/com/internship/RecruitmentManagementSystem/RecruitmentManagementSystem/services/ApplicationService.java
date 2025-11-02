package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ShortlistedApplicationStaus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ApplicationServiceInterface;
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
public class ApplicationService implements ApplicationServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final PositionRepository positionRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final CandidateRepository candidateRepository;
    private final ShortlistedApplicationRepository shortlistedApplicationRepository;
    private final ShortlistedApplicationStatusRepository shortlistedApplicationStatusRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
    })
    public ApplicationDto addApplication(ApplicationDto newApplication) {
        logger.info("Adding new application for positionId: {}", newApplication.getPositionId());
        PositionModel position = positionRepository.findById(newApplication.getPositionId()).orElseThrow(
                ()->new ResourceNotFoundException("Position","positionId",newApplication.getPositionId().toString())
        );

        CandidateModel candidate = candidateRepository.findById(newApplication.getCandidateId()).orElseThrow(
                ()->new ResourceNotFoundException("Candidate","candidateId",newApplication.getCandidateId().toString())
        );

        ApplicationStatusModel applicationStatus = new ApplicationStatusModel();
        applicationStatus.setApplicationStatus(ApplicationStatus.valueOf("UNDERPROCESS"));
        applicationStatus.setApplicationFeedback("Your Application Is Under Evaluation !");

        ApplicationStatusModel savedApplicationStatus = applicationStatusRepository.save(applicationStatus);

        ApplicationModel application = new ApplicationModel();
        application.setPosition(position);
        application.setCandidate(candidate);
        application.setApplicationStatus(savedApplicationStatus);

        ApplicationModel savedApplication = applicationRepository.save(application);
        logger.info("Application added successfully with ID: {}", savedApplication.getApplicationId());
        return converter(savedApplication);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "shortlistedApplicationData",allEntries = true)
    })
    public ApplicationDto updateApplication(Integer applicationId, ApplicationDto newApplication) {
        logger.info("Updating application with ID: {}", applicationId);
        ApplicationModel existingApplication = applicationRepository.findById(applicationId).orElseThrow(
                ()->new ResourceNotFoundException("Application","applicationId",applicationId.toString())
        );

        if(newApplication.getPositionId() != null){
            PositionModel newPosition = positionRepository.findById(newApplication.getPositionId()).orElseThrow(
                    ()->new ResourceNotFoundException("Position","positionId",newApplication.getPositionId().toString())
            );
            existingApplication.setPosition(newPosition);
        }
        if(newApplication.getCandidateId() != null){
            CandidateModel candidate = candidateRepository.findById(newApplication.getCandidateId()).orElseThrow(
                    ()->new ResourceNotFoundException("Candidate","candidateId",newApplication.getCandidateId().toString())
            );
            existingApplication.setCandidate(candidate);
        }
        if(newApplication.getApplicationStatus() != null && newApplication.getApplicationStatus().getApplicationStatus() != null){
            if (newApplication.getApplicationStatus().getApplicationStatus().equals(ApplicationStatus.valueOf("ACCEPTED"))){
                shortListApplication(applicationId);
            }
            existingApplication.getApplicationStatus().setApplicationStatus(newApplication.getApplicationStatus().getApplicationStatus());
        }
        if(newApplication.getApplicationStatus() != null && newApplication.getApplicationStatus().getApplicationFeedback() != null){
            existingApplication.getApplicationStatus().setApplicationFeedback(newApplication.getApplicationStatus().getApplicationFeedback());
        }

        ApplicationModel updatedApplication = applicationRepository.save(existingApplication);
        logger.info("Application updated successfully with ID: {}", updatedApplication.getApplicationId());
        return converter(updatedApplication);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true)
    })
    public ApplicationStatusDto updateApplicationStatus(Integer applicationId,Integer applicationStatusId, ApplicationStatusDto newApplicationStatus) {
        ApplicationStatusModel existingApplicationStatus = applicationStatusRepository.findById(applicationStatusId).orElseThrow(
                ()->new ResourceNotFoundException("ApplicationStatus","applicationStatusId",applicationStatusId.toString())
        );
        if(newApplicationStatus.getApplicationStatus() != null){
            existingApplicationStatus.setApplicationStatus(newApplicationStatus.getApplicationStatus());
        }

        if(newApplicationStatus.getApplicationFeedback() != null){
            existingApplicationStatus.setApplicationFeedback(newApplicationStatus.getApplicationFeedback());
        }

        ApplicationStatusModel updatedApplicationStatus = applicationStatusRepository.save(existingApplicationStatus);
        return converter(updatedApplicationStatus);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'application_id_'+#applicationId")
    public ApplicationDto getApplication(Integer applicationId) {
        logger.info("Fetching application with ID: {}", applicationId);
        ApplicationModel application = applicationRepository.findById(applicationId).orElseThrow(
                () -> new ResourceNotFoundException("Application","applicationId",applicationId.toString())
        );
        logger.info("Fetched application successfully for ID: {}", applicationId);
        return converter(application);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationDto> getAllApplications(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationDto> response = getPaginatedApplications(applicationRepository.findAll(getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} applications", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'applications_applied_'+#candidateId")
    public List<Integer> getCandidateApplicationId(Integer candidateId) {
        return applicationRepository.findAppliedPositionIdsByCandidateId(candidateId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true)
    })
    public void deleteApplication(Integer applicationId) {
        logger.info("Deleting application with ID: {}", applicationId);
        ApplicationModel existingApplication = applicationRepository.findById(applicationId).orElseThrow(
                ()->new ResourceNotFoundException("Application","applicationId",applicationId.toString())
        );
        applicationRepository.delete(existingApplication);
        logger.info("Deleted application successfully with ID: {}", applicationId);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'application_count'")
    public Long countTotalApplications() {
        logger.info("Counting total applications");
        Long count = applicationRepository.countApplications();
        logger.info("Total applications count: {}", count);
        return count;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'candidate_'+#candidateId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationDto> getCandidateApplications(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications Of candidateId : {}  - page: {}, size: {}, sortBy: {}, sortDir: {}",candidateId, page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationDto> response = getPaginatedApplications(applicationRepository.findByCandidateCandidateId(candidateId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} applications of candidateId : {}", response.getData().size(),candidateId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'position_'+#positionId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationDto> getPositionApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications Of positionId : {}  - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationDto> response = getPaginatedApplications(applicationRepository.findByPositionPositionId(positionId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} applications of positionId : {}", response.getData().size(),positionId);
        return response;
    }

    @Transactional
    private void shortListApplication(Integer applicationId) {
        logger.info("Shortlisting application with ApplicationId: {}", applicationId);

        ShortlistedApplicationStatusModel shortlistedApplicationStatus = new ShortlistedApplicationStatusModel();
        shortlistedApplicationStatus.setShortlistedApplicationStatus(ShortlistedApplicationStaus.SHORTLISTED);
        shortlistedApplicationStatus.setShortlistedApplicationFeedback("Your Application Is Shortlisted For this Position, Wait For Your Interview Schedule !");

        ShortlistedApplicationStatusModel savedShortlistedApplicationStatus = shortlistedApplicationStatusRepository.save(shortlistedApplicationStatus);
        logger.debug("Saved ShortlistedApplicationStatus with ID: {}", savedShortlistedApplicationStatus.getShortlistedApplicationStatusId());

        ApplicationModel application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    logger.error("Application not found with ID: {}", applicationId);
                    return new ResourceNotFoundException("Application", "applicationId", applicationId.toString());
                });

        ShortlistedApplicationModel newShortlistedApplication = new ShortlistedApplicationModel();
        newShortlistedApplication.setApplication(application);
        newShortlistedApplication.setShortlistedApplicationStatus(savedShortlistedApplicationStatus);

        ShortlistedApplicationModel savedShortlistedApplication = shortlistedApplicationRepository.save(newShortlistedApplication);
        logger.info("Shortlisted application saved successfully with ID: {}", savedShortlistedApplication.getShortlistedApplicationId());

    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PaginatedResponse<ApplicationDto> getPaginatedApplications(Page<ApplicationModel> pageResponse) {
        PaginatedResponse<ApplicationDto> response = new PaginatedResponse<>();
        response.setData(convertContent(pageResponse.getContent()));
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        return response;
    }

    private List<ApplicationDto> convertContent(List<ApplicationModel> content) {
        List<ApplicationDto> applications = new ArrayList<>();
        content.forEach(application -> {
            applications.add(converter(application));
        });
        return applications;
    }

    protected ApplicationDto converter(ApplicationModel entity){
        ApplicationDto dto = new ApplicationDto();
        dto.setApplicationId(entity.getApplicationId());
        dto.setPositionId(entity.getPosition().getPositionId());
        dto.setCandidateId(entity.getCandidate().getCandidateId());
        dto.setApplicationStatus(converter(entity.getApplicationStatus()));
        return dto;
    }
    private ApplicationStatusDto converter(ApplicationStatusModel entity){
        ApplicationStatusDto dto = new ApplicationStatusDto();
        dto.setApplicationStatusId(entity.getApplicationStatusId());
        dto.setApplicationStatus(entity.getApplicationStatus());
        dto.setApplicationFeedback(entity.getApplicationFeedback());
        return dto;
    }

}
