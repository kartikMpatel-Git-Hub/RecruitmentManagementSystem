package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ShortlistedApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ShortlistedApplicationStatusDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ShortlistedApplicationStaus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ShortlistedApplicationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ShortlistedApplicationStatusModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.ApplicationRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.ShortlistedApplicationRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.ShortlistedApplicationStatusRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ShortlistedApplicationServiceInterface;
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
@Deprecated
public class ShortlistedApplicationService
//        implements ShortlistedApplicationServiceInterface
{

//    private static final Logger logger = LoggerFactory.getLogger(ShortlistedApplicationService.class);
//
//    private final ApplicationRepository applicationRepository;
//    private final ShortlistedApplicationRepository shortlistedApplicationRepository;
//    private final ShortlistedApplicationStatusRepository shortlistedApplicationStatusRepository;
//    private final ApplicationService applicationService;
//
//    @Override
//    @Transactional
//    @Caching(evict = {
//            @CacheEvict(value = "shortlistedApplicationData",allEntries = true)
//    })
//    public ShortlistedApplicationDto shortListApplication(ShortlistedApplicationDto shortlistedApplication) {
//        logger.info("Shortlisting application with ApplicationId: {}", shortlistedApplication.getApplication().getApplicationId());
//
//        ShortlistedApplicationStatusModel shortlistedApplicationStatus = new ShortlistedApplicationStatusModel();
//        shortlistedApplicationStatus.setShortlistedApplicationStatus(ShortlistedApplicationStaus.SHORTLISTED);
//        shortlistedApplicationStatus.setShortlistedApplicationFeedback("Your Application Is Shortlisted For this Position, Wait For Your Interview Schedule !");
//
//        ShortlistedApplicationStatusModel savedShortlistedApplicationStatus = shortlistedApplicationStatusRepository.save(shortlistedApplicationStatus);
//        logger.debug("Saved ShortlistedApplicationStatus with ID: {}", savedShortlistedApplicationStatus.getShortlistedApplicationStatusId());
//
//        ApplicationModel application = applicationRepository.findById(shortlistedApplication.getApplication().getApplicationId())
//                .orElseThrow(() -> {
//                    logger.error("Application not found with ID: {}", shortlistedApplication.getApplication().getApplicationId());
//                    return new ResourceNotFoundException("Application", "applicationId", shortlistedApplication.getApplication().getApplicationId().toString());
//                });
//
//        ShortlistedApplicationModel newShortlistedApplication = new ShortlistedApplicationModel();
//        newShortlistedApplication.setApplication(application);
//        newShortlistedApplication.setShortlistedApplicationStatus(savedShortlistedApplicationStatus);
//
//        ShortlistedApplicationModel savedShortlistedApplication = shortlistedApplicationRepository.save(newShortlistedApplication);
//        logger.info("Shortlisted application saved successfully with ID: {}", savedShortlistedApplication.getShortlistedApplicationId());
//
//        return convertor(savedShortlistedApplication);
//    }
//
//    @Override
//    @Transactional
//    @Caching(evict = {
//            @CacheEvict(value = "shortlistedApplicationData",allEntries = true)
//    })
//    public ShortlistedApplicationStatusDto updateShortlistedApplicationStatus(Integer shortlistedApplicationStatusId, ShortlistedApplicationStatusDto shortlistedApplicationStatus) {
//        logger.info("Updating shortlisted application status with ID: {}", shortlistedApplicationStatusId);
//
//        ShortlistedApplicationStatusModel existingShortlistedApplicationStatus = shortlistedApplicationStatusRepository.findById(shortlistedApplicationStatusId)
//                .orElseThrow(() -> {
//                    logger.error("ShortlistedApplicationStatus not found with ID: {}", shortlistedApplicationStatusId);
//                    return new ResourceNotFoundException("ShortlistedApplicationStatus", "shortlistedApplicationStatusId", shortlistedApplicationStatusId.toString());
//                });
//
//        if (shortlistedApplicationStatus.getShortlistedApplicationStatus() != null) {
//            logger.debug("Updating status to: {}", shortlistedApplicationStatus.getShortlistedApplicationStatus());
//            existingShortlistedApplicationStatus.setShortlistedApplicationStatus(shortlistedApplicationStatus.getShortlistedApplicationStatus());
//        }
//
//        if (shortlistedApplicationStatus.getShortlistedApplicationFeedback() != null) {
//            logger.debug("Updating feedback to: {}", shortlistedApplicationStatus.getShortlistedApplicationFeedback());
//            existingShortlistedApplicationStatus.setShortlistedApplicationFeedback(shortlistedApplicationStatus.getShortlistedApplicationFeedback());
//        }
//
//        ShortlistedApplicationStatusModel updatedShortlistedApplicationStatus = shortlistedApplicationStatusRepository.save(existingShortlistedApplicationStatus);
//        logger.info("Shortlisted application status updated successfully for ID: {}", shortlistedApplicationStatusId);
//
//        return convertor(updatedShortlistedApplicationStatus);
//    }
//
//    @Override
//    @Cacheable(value = "shortlistedApplicationData",key = "'shortlisted_application_id_'+#shortlistedApplicationId")
//    public ShortlistedApplicationDto getShortlistedApplication(Integer shortlistedApplicationId) {
//        logger.info("Fetching shortlisted application with ID: {}", shortlistedApplicationId);
//
//        ShortlistedApplicationModel shortlistedApplication = shortlistedApplicationRepository.findById(shortlistedApplicationId)
//                .orElseThrow(() -> {
//                    logger.error("ShortlistedApplication not found with ID: {}", shortlistedApplicationId);
//                    return new ResourceNotFoundException("ShortlistedApplication", "shortlistedApplicationId", shortlistedApplicationId.toString());
//                });
//
//        logger.info("Fetched shortlisted application successfully with ID: {}", shortlistedApplicationId);
//        return convertor(shortlistedApplication);
//    }
//
//    @Override
//    @Cacheable(value = "shortlistedApplicationData",key = "'shortlisted_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
//    public PaginatedResponse<ShortlistedApplicationDto> getAllShortlistedApplications(Integer page, Integer size, String sortBy, String sortDir) {
//        logger.info("Fetching all shortlisted applications - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
//
//        var shortlistedApplications = shortlistedApplicationRepository.findAll(getPageable(page, size, sortBy, sortDir));
//        logger.debug("Found {} shortlisted applications", shortlistedApplications.getContent().size());
//
//        return getPaginatedShortlistedApplications(shortlistedApplications);
//    }
//
//    @Override
//    @Cacheable(value = "shortlistedApplicationData",key = "'shortlisted_candidate_'+#candidateId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
//    public PaginatedResponse<ShortlistedApplicationDto> getCandidateShortlistedApplications(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
//        logger.info("Fetching shortlisted applications for candidateId: {}", candidateId);
//
//        var shortlistedCandidateApplications = shortlistedApplicationRepository.findByApplicationCandidateCandidateId(candidateId, getPageable(page, size, sortBy, sortDir));
//        logger.debug("Found {} shortlisted applications for candidateId: {}", shortlistedCandidateApplications.getContent().size(), candidateId);
//
//        return getPaginatedShortlistedApplications(shortlistedCandidateApplications);
//    }
//
//    @Override
//    @Cacheable(value = "shortlistedApplicationData",key = "'shortlisted_position_'+#positionId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
//    public PaginatedResponse<ShortlistedApplicationDto> getPositionShortlistedApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
//        logger.info("Fetching shortlisted applications for positionId: {}", positionId);
//
//        var shortlistedPositionApplications = shortlistedApplicationRepository.findByApplicationPositionPositionId(positionId, getPageable(page, size, sortBy, sortDir));
//        logger.debug("Found {} shortlisted applications for positionId: {}", shortlistedPositionApplications.getContent().size(), positionId);
//
//        return getPaginatedShortlistedApplications(shortlistedPositionApplications);
//    }
//
//    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
//        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
//                ? Sort.by(sortBy).ascending()
//                : Sort.by(sortBy).descending();
//        return PageRequest.of(page, size, sort);
//    }
//
//    private PaginatedResponse<ShortlistedApplicationDto> getPaginatedShortlistedApplications(Page<ShortlistedApplicationModel> pageResponse) {
//        PaginatedResponse<ShortlistedApplicationDto> response = new PaginatedResponse<>();
//        response.setData(convertContent(pageResponse.getContent()));
//        response.setCurrentPage(pageResponse.getNumber());
//        response.setLast(pageResponse.isLast());
//        response.setPageSize(pageResponse.getSize());
//        response.setTotalItems(pageResponse.getTotalElements());
//        response.setTotalPages(pageResponse.getTotalPages());
//
//        logger.info("Paginated shortlisted applications: Page {}/{} | Total: {}", response.getCurrentPage() + 1, response.getTotalPages(), response.getTotalItems());
//        return response;
//    }
//
//    private List<ShortlistedApplicationDto> convertContent(List<ShortlistedApplicationModel> content) {
//        List<ShortlistedApplicationDto> shortlistedApplications = new ArrayList<>();
//        content.forEach(application -> shortlistedApplications.add(convertor(application)));
//        return shortlistedApplications;
//    }
//
//    private ShortlistedApplicationDto convertor(ShortlistedApplicationModel entity) {
//        ShortlistedApplicationDto dto = new ShortlistedApplicationDto();
//        dto.setShortlistedApplicationId(entity.getShortlistedApplicationId());
//        dto.setApplication(applicationService.converter(entity.getApplication()));
//        dto.setShortlistedApplicationStatus(convertor(entity.getShortlistedApplicationStatus()));
//        return dto;
//    }
//
//    private ShortlistedApplicationStatusDto convertor(ShortlistedApplicationStatusModel entity) {
//        ShortlistedApplicationStatusDto dto = new ShortlistedApplicationStatusDto();
//        dto.setShortlistedApplicationStatusId(entity.getShortlistedApplicationStatusId());
//        dto.setShortlistedApplicationStatus(entity.getShortlistedApplicationStatus());
//        dto.setShortlistedApplicationFeedback(entity.getShortlistedApplicationFeedback());
//        return dto;
//    }
}