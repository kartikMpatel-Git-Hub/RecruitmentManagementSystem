package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentVerificationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ApplicationServiceInterface;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService implements ApplicationServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private final MatchingScoreService matchingScoreService;
    private final PaginatedResponseCreator paginatedResponseCreator;
    private final ModelServiceInterface modelService;
    private final Mapper mapper;
    private final EmailService mailService;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public ApplicationResponseDto addApplication(ApplicationCreateDto newApplication) {
        logger.info("Adding new application for positionId: {}", newApplication.getPositionId());
        PositionModel position = modelService.getPosition(newApplication.getPositionId());

        CandidateModel candidate = modelService.getCandidate(newApplication.getCandidateId());

        if(modelService.existApplication(candidate.getCandidateId(),position.getPositionId())){
            throw new FailedProcessException("Filed to apply ! You have already applied for this position.");
        }

        Double score = matchingScoreService.calculateMatchingScore(candidate,position);
        return createApplication(candidate,position,ApplicationStatus.UNDERPROCESS,score);
    }

    private ApplicationResponseDto createApplication(CandidateModel candidate,PositionModel position,ApplicationStatus status,Double score){

        ApplicationStatusModel applicationStatus = new ApplicationStatusModel();
        applicationStatus.setApplicationStatus(status);
        applicationStatus.setApplicationFeedback("Your Application Is Under Evaluation !");

        ApplicationModel application = new ApplicationModel();
        application.setPosition(position);
        application.setCandidate(candidate);
        application.setMatchScore(score);
        application.setIsShortlisted(false);
        application.setApplicationStatus(applicationStatus);

        ApplicationModel savedApplication = modelService.addApplication(application);

        logger.info("Application added successfully with ID: {}", savedApplication.getApplicationId());
        return mapper.toDto(savedApplication, ApplicationResponseDto.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public void shortlistApplication(Integer applicationId) {
        ApplicationModel application = modelService.getApplication(applicationId);
        if(application.getIsSelected()==Boolean.TRUE)
            return;
        ApplicationStatusModel applicationStatus = application.getApplicationStatus();
        applicationStatus.setApplicationStatus(ApplicationStatus.valueOf("SHORTLISTED"));
        applicationStatus.setApplicationFeedback("Your Application Is Shortlisted For Next Round !");
        application.setApplicationStatus(applicationStatus);
        UserModel currentUser = modelService.getCurrentUser();
        application.setShortlistedBy(currentUser);
        application.setIsShortlisted(true);

        List<RoundModel> rounds = application.getPosition().getPositionRounds().stream().map(
                round -> {
                    RoundModel newRound = new RoundModel();
                    newRound.setApplication(application);
                    newRound.setRoundSequence(round.getPositionRoundSequence());
                    newRound.setRoundType(round.getPositionRoundType());
                    newRound.setRoundResult(RoundResult.valueOf("PENDING"));
                    newRound.setRoundFeedback("");
                    newRound.setRoundRating(0D);
                    return newRound;
                }
        ).toList();
        modelService.addAllRound(rounds);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public void moveToDocumentVerification(Integer applicationId) {
        ApplicationModel application = modelService.getApplication(applicationId);

        ApplicationStatusModel applicationStatus = application.getApplicationStatus();
        applicationStatus.setApplicationStatus(ApplicationStatus.DOCUMENT_VERIFICATION);
        applicationStatus.setApplicationFeedback("You Are successfully Pass All Rounds !");
        DocumentVerificationModel documentVerificationModel = new DocumentVerificationModel();
        documentVerificationModel.setApplication(application);
        documentVerificationModel.setHrRemarks("Submit Your Document For Evaluation !");
        documentVerificationModel.setVerificationStatus(DocumentVerificationStatus.PENDING);
        documentVerificationModel.setVerifiedAt(LocalDateTime.now());
        documentVerificationModel.setVerifiedBy(modelService.getCurrentUser());
        modelService.addDocumentVerification(documentVerificationModel);
        mailService.mailToCandidate(application.getCandidate().getUser().getUsername(),
                    application.getCandidate().getUser().getUserEmail(),
                    application.getPosition().getPositionTitle()
                );
    }

    @Override
    @Cacheable(value = "applicationData",key = "'mapped_applications_positionId_'+#positionId+'page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<MappedApplicationResponseDto> getMatchedApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Mapped applications Of Position : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<MappedApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getPositionApplicationByStatus(positionId, ApplicationStatus.MAPPED, pageable)
                        , MappedApplicationResponseDto.class);
        logger.info("Fetched {} Mapped applications of PositionId : {} ", response.getData().size(),positionId);
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public Integer matchApplicationsForPosition(Integer positionId, Integer thresholdScore) {

        logger.info("Starting automatic matching for PositionId: {} with threshold score: {}", positionId, thresholdScore);

        PositionModel position = modelService.getPosition(positionId);

        double heistScore = 0.0;

        logger.debug("Fetched position details: {} - {}", position.getPositionId(), position.getPositionTitle());

        List<CandidateModel> candidates = modelService.getAllCandidates();
        logger.debug("Total candidates fetched for matching: {}", candidates.size());

        List<ApplicationResponseDto> matchedApplications = new ArrayList<>();

        for(CandidateModel candidate : candidates){

            logger.debug("Checking candidateId: {} for existing application against positionId: {}",
                    candidate.getCandidateId(), position.getPositionId());

            if (modelService.existApplication(candidate.getCandidateId(), position.getPositionId())) {

                logger.info("CandidateId: {} already has an application for PositionId: {} → Skipping",
                        candidate.getCandidateId(), position.getPositionId());
                continue;
            }

            double score = matchingScoreService.calculateMatchingScore(candidate, position);

            if(score > heistScore)
                heistScore = score;
            logger.info("Matching score calculated → CandidateId: {}, PositionId: {}, Score: {}",
                    candidate.getCandidateId(), position.getPositionId(), score);

            if (score >= thresholdScore) {
                logger.info(
                        "CandidateId: {} PASSED threshold (Score: {} ≥ Threshold: {}) → Creating mapped application",
                        candidate.getCandidateId(), score, thresholdScore
                );
                matchedApplications.add(createApplication(candidate, position, ApplicationStatus.MAPPED, score));
            } else {
                logger.debug(
                        "CandidateId: {} FAILED threshold (Score: {} < Threshold: {})",
                        candidate.getCandidateId(), score, thresholdScore
                );
            }
        }

        logger.info("Matching process completed for PositionId: {} → Total matched applications: {}",
                positionId, matchedApplications.size());

        if(matchedApplications.isEmpty()){
            throw new ResourceNotFoundException("Match Applications",thresholdScore + " Threshold Heist Score Is",heistScore+"");
        }

        return matchedApplications.size();
    }


    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ShortlistedApplicationResponseDto> getAllShortlistedApplications(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ShortlistedApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getShortlistedApplications(pageable), ShortlistedApplicationResponseDto.class
                );
        logger.info("Fetched {} Shortlisted applications ", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_recruiter_id'+#recruiterId+'_page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ShortlistedApplicationResponseDto> getAllShortlistedApplicationsByRecruiter(Integer recruiterId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications By Recruiter- page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ShortlistedApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getRecruiterShortlistedApplications(recruiterId,pageable)
                        , ShortlistedApplicationResponseDto.class);
        logger.info("Fetched {} Shortlisted applications By Recruiter : {}", response.getData().size(),recruiterId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_position_'+#positionId+'_page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ShortlistedApplicationResponseDto> getPositionShortlistedApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications Of Position : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ShortlistedApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(modelService.getPositionShortlistedApplications(positionId,pageable)
                        , ShortlistedApplicationResponseDto.class);
        logger.info("Fetched {} Shortlisted applications of PositionId : {} ", response.getData().size(),positionId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_candidate_+'#candidateId'+_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ShortlistedApplicationResponseDto> getCandidateShortlistedApplications(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications Of Candidate : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",candidateId, page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ShortlistedApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getCandidateShortlistedApplications(candidateId,pageable)
                        , ShortlistedApplicationResponseDto.class);
        logger.info("Fetched {} Shortlisted applications of CandidateId : {} ", response.getData().size(),candidateId);
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public ApplicationStatusResponseDto updateApplicationStatus(Integer applicationId,Integer applicationStatusId, ApplicationStatusUpdateDto newApplicationStatus) {
        ApplicationStatusModel existingApplicationStatus = modelService.getApplicationStatus(applicationStatusId);
        if(newApplicationStatus.getApplicationStatus() != null){
            existingApplicationStatus.setApplicationStatus(newApplicationStatus.getApplicationStatus());
            if(newApplicationStatus.getApplicationStatus() != null && newApplicationStatus.getApplicationStatus().equals(ApplicationStatus.REJECTED)){
                existingApplicationStatus.setApplicationFeedback("Your Are Rejected For This Position !");
            }
            if(newApplicationStatus.getApplicationStatus() != null && newApplicationStatus.getApplicationStatus().equals(ApplicationStatus.DOCUMENT_VERIFICATION)){
                existingApplicationStatus.setApplicationFeedback("You Are successfully Pass All Rounds !");
                DocumentVerificationModel documentVerificationModel = new DocumentVerificationModel();
                ApplicationModel application = modelService.getApplication(applicationId);
                documentVerificationModel.setApplication(application);
                documentVerificationModel.setHrRemarks("Submit Your Document For Evaluation !");
                documentVerificationModel.setVerificationStatus(DocumentVerificationStatus.PENDING);
                documentVerificationModel.setVerifiedAt(LocalDateTime.now());
                documentVerificationModel.setVerifiedBy(modelService.getCurrentUser());
                modelService.addDocumentVerification(documentVerificationModel);
            }
            if(newApplicationStatus.getApplicationStatus() != null && newApplicationStatus.getApplicationStatus().equals(ApplicationStatus.REJECTED)){
                ApplicationModel application = modelService.getApplication(applicationId);
                application.getApplicationStatus().setApplicationStatus(ApplicationStatus.REJECTED);
                application.getRounds().forEach(r -> {
                    if(r.getRoundResult().equals(RoundResult.PENDING)){
                        r.setRoundResult(RoundResult.CANCELLED);
                    }
                });
                modelService.addApplication(application);
            }
        }

        if(newApplicationStatus.getApplicationFeedback() != null){
            existingApplicationStatus.setApplicationFeedback(newApplicationStatus.getApplicationFeedback());
        }else{
            if(newApplicationStatus.getApplicationStatus() != null && newApplicationStatus.getApplicationStatus().equals(ApplicationStatus.REJECTED)){
                existingApplicationStatus.setApplicationFeedback("Your Are Rejected For This Position !");
            }
        }
        ApplicationStatusModel updatedApplicationStatus = modelService.addApplicationStatus(existingApplicationStatus);
        return mapper.toDto(updatedApplicationStatus, ApplicationStatusResponseDto.class);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'application_id_'+#applicationId")
    public ApplicationRoundResponseDto getApplication(Integer applicationId) {
        logger.info("Fetching application with ID: {}", applicationId);
        ApplicationModel application = modelService.getApplication(applicationId);
        logger.info("Fetched application successfully for ID: {}", applicationId);
        return mapper.toDto(application, ApplicationRoundResponseDto.class);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getAllApplications(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getAllApplications(pageable)
                        , ApplicationResponseDto.class
                );
        logger.info("Fetched {} applications", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'applications_recruiter_id'+#recruiterId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getAllApplicationsByRecruiter(Integer recruiterId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications By Recruiter- page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getRecruiterApplications(recruiterId,pageable)
                        , ApplicationResponseDto.class
                );
        logger.info("Fetched {} applications By Recruiter", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'applications_applied_'+#candidateId")
    public List<Integer> getCandidateApplicationId(Integer candidateId) {
        return modelService.getCandidateApplicationIds(candidateId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public void deleteApplication(Integer applicationId) {
        logger.info("Deleting application with ID: {}", applicationId);
        ApplicationModel existingApplication = modelService.getApplication(applicationId);
        modelService.removeApplication(existingApplication);
        logger.info("Deleted application successfully with ID: {}", applicationId);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'application_count'")
    public Long countTotalApplications() {
        logger.info("Counting total applications");
        Long count = modelService.countApplications();
        logger.info("Total applications count: {}", count);
        return count;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'candidate_'+#candidateId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getCandidateApplications(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications Of candidateId : {}  - page: {}, size: {}, sortBy: {}, sortDir: {}",candidateId, page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(
                        modelService.getCandidateApplications(candidateId,pageable)
                        , ApplicationResponseDto.class
                );
        logger.info("Fetched {} applications of candidateId : {}", response.getData().size(),candidateId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'position_'+#positionId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getPositionApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications Of positionId : {}  - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response = paginatedResponseCreator.getPaginatedResponse(
                modelService.getPositionApplications(positionId,pageable)
                , ApplicationResponseDto.class
        );
        logger.info("Fetched {} applications of positionId : {}", response.getData().size(),positionId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'reviewer_'+#reviewerId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ShortlistedApplicationResponseDto> getAllShortlistedApplicationsByReviewer(Integer reviewerId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications By Reviewer - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ShortlistedApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(modelService.getReviewerShortlistedApplications(reviewerId,pageable)
                        , ShortlistedApplicationResponseDto.class);
        logger.info("Fetched {} Shortlisted applications By Reviewer", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'reviewer_'+#reviewerId+'_positionId_'+#positionId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ShortlistedApplicationResponseDto> getPositionShortlistedApplicationsByReviewer(Integer positionId, Integer reviewerId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Positions Shortlisted applications By Reviewer - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        PaginatedResponse<ShortlistedApplicationResponseDto> response =
                paginatedResponseCreator.getPaginatedResponse(modelService.getPositionReviewerShortlistedApplications(positionId,reviewerId,pageable)
                        , ShortlistedApplicationResponseDto.class);
        logger.info("Fetched {} Shortlisted applications By Position And Reviewer", response.getData().size());
        return response;
    }

}
