package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application.ApplicationResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application.ApplicationStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewInterviewerResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewerFeedbackResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillRatingResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserMinimalResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService implements ApplicationServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final PositionRepository positionRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final CandidateRepository candidateRepository;
    private final RoundRepository roundRepository;
    private final MatchingScoreService matchingScoreService;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "positionData",allEntries = true),
    })
    public ApplicationResponseDto addApplication(ApplicationCreateDto newApplication) {
        logger.info("Adding new application for positionId: {}", newApplication.getPositionId());
        PositionModel position = positionRepository.findById(newApplication.getPositionId()).orElseThrow(
                ()->new ResourceNotFoundException("Position","positionId",newApplication.getPositionId().toString())
        );

        CandidateModel candidate = candidateRepository.findById(newApplication.getCandidateId()).orElseThrow(
                ()->new ResourceNotFoundException("Candidate","candidateId",newApplication.getCandidateId().toString())
        );

        if(applicationRepository.existsByCandidateCandidateIdAndPositionPositionId(candidate.getCandidateId(),position.getPositionId())){
            throw new FailedProcessException("Filed to apply ! You have already applied for this position.");
        }

        Double score = matchingScoreService.calculateMatchingScore(candidate,position);
        return createApplication(candidate,position,ApplicationStatus.UNDERPROCESS,score);
    }

    private ApplicationResponseDto createApplication(CandidateModel candidate,PositionModel position,ApplicationStatus status,Double score){

        ApplicationStatusModel applicationStatus = new ApplicationStatusModel();
        applicationStatus.setApplicationStatus(status);
        applicationStatus.setApplicationFeedback("Your Application Is Under Evaluation !");

        ApplicationStatusModel savedApplicationStatus = applicationStatusRepository.save(applicationStatus);

        ApplicationModel application = new ApplicationModel();
        application.setPosition(position);
        application.setCandidate(candidate);
        application.setMatchScore(score);
        application.setIsShortlisted(false);
        application.setApplicationStatus(savedApplicationStatus);

        ApplicationModel savedApplication = applicationRepository.save(application);
        logger.info("Application added successfully with ID: {}", savedApplication.getApplicationId());
        return converter(savedApplication);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
    })
    public void shortlistApplication(Integer applicationId) {
        ApplicationModel application = applicationRepository.findById(applicationId).orElseThrow(
                ()->new ResourceNotFoundException("Application","applicationId",applicationId.toString())
        );

        ApplicationStatusModel applicationStatus = application.getApplicationStatus();
        applicationStatus.setApplicationStatus(ApplicationStatus.valueOf("SHORTLISTED"));
        applicationStatus.setApplicationFeedback("Your Application Is Shortlisted For Next Round !");
        application.setApplicationStatus(applicationStatus);
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        application.setShortlistedBy(currentUser);
        application.setIsShortlisted(true);

        application.getPosition().getPositionRounds().forEach(
                round -> {
                    RoundModel newRound = new RoundModel();
                    newRound.setApplication(application);
                    newRound.setRoundSequence(round.getPositionRoundSequence());
                    newRound.setRoundType(round.getPositionRoundType());
                    newRound.setRoundResult(RoundResult.valueOf("PENDING"));
                    newRound.setRoundFeedback("");
                    newRound.setRoundRating(0D);
                    roundRepository.save(newRound);
                }
        );

        applicationRepository.save(application);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'mapped_applications_positionId_'+#positionId+'page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getMatchedApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Mapped applications Of Position : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response = getPaginatedApplications(
                applicationRepository.findByApplicationStatusApplicationStatusAndPositionPositionId(ApplicationStatus.MAPPED,positionId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} Mapped applications of PositionId : {} ", response.getData().size(),positionId);
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData", allEntries = true),
    })
    public Integer matchApplicationsForPosition(Integer positionId, Integer thresholdScore) {

        logger.info("Starting automatic matching for PositionId: {} with threshold score: {}", positionId, thresholdScore);

        PositionModel position = positionRepository.findById(positionId).orElseThrow(() -> {
            logger.error("Position not found for PositionId: {}", positionId);
            return new ResourceNotFoundException("Position", "positionId", positionId.toString());
        });

        Double heistScore = 0.0;

        logger.debug("Fetched position details: {} - {}", position.getPositionId(), position.getPositionTitle());

        List<CandidateModel> candidates = candidateRepository.findAll();
        logger.debug("Total candidates fetched for matching: {}", candidates.size());

        List<ApplicationResponseDto> matchedApplications = new ArrayList<>();

        for(CandidateModel candidate : candidates){

            logger.debug("Checking candidateId: {} for existing application against positionId: {}",
                    candidate.getCandidateId(), position.getPositionId());

            if (applicationRepository.existsByCandidateCandidateIdAndPositionPositionId(
                    candidate.getCandidateId(), position.getPositionId())) {

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
            throw new ResourceNotFoundException("Match Applications",thresholdScore + " Threshold Heist Score Is",heistScore.toString());
        }

        return matchedApplications.size();
    }


    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getAllShortlistedApplications(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                getPaginatedApplications(applicationRepository.findByIsShortlistedTrue(getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} Shortlisted applications ", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_recruiter_id'+#recruiterId+'_page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getAllShortlistedApplicationsByRecruiter(Integer recruiterId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications By Recruiter- page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                getPaginatedApplications(applicationRepository.findRecruiterShortlists(recruiterId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} Shortlisted applications By Recruiter", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_position_'+#positionId+'_page_'+#page+'_size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getPositionShortlistedApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications Of Position : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response = getPaginatedApplications(
                applicationRepository.findByIsShortlistedTrueAndPositionPositionId(positionId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} Shortlisted applications of PositionId : {} ", response.getData().size(),positionId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'shortlisted_applications_candidate_+'#candidateId'+_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getCandidateShortlistedApplications(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications Of Candidate : {} - page: {}, size: {}, sortBy: {}, sortDir: {}",candidateId, page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response = getPaginatedApplications(
                applicationRepository.findByIsShortlistedTrueAndCandidateCandidateId(candidateId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} Shortlisted applications of CandidateId : {} ", response.getData().size(),candidateId);
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true)
    })
    public ApplicationStatusResponseDto updateApplicationStatus(Integer applicationId,Integer applicationStatusId, ApplicationStatusUpdateDto newApplicationStatus) {
        ApplicationStatusModel existingApplicationStatus = applicationStatusRepository.findById(applicationStatusId).orElseThrow(
                ()->new ResourceNotFoundException("ApplicationStatus","applicationStatusId",applicationStatusId.toString())
        );
        if(newApplicationStatus.getApplicationStatus() != null){
            existingApplicationStatus.setApplicationStatus(newApplicationStatus.getApplicationStatus());
        }

        if(newApplicationStatus.getApplicationFeedback() != null){
            existingApplicationStatus.setApplicationFeedback(newApplicationStatus.getApplicationFeedback());
        }else{
            if(newApplicationStatus.getApplicationStatus() != null && newApplicationStatus.getApplicationStatus().equals(ApplicationStatus.REJECTED)){
                existingApplicationStatus.setApplicationFeedback("Your Are Rejected For This Position !");
            }
        }

        ApplicationStatusModel updatedApplicationStatus = applicationStatusRepository.save(existingApplicationStatus);
        return converter(updatedApplicationStatus);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'application_id_'+#applicationId")
    public ApplicationResponseDto getApplication(Integer applicationId) {
        logger.info("Fetching application with ID: {}", applicationId);
        ApplicationModel application = applicationRepository.findById(applicationId).orElseThrow(
                () -> new ResourceNotFoundException("Application","applicationId",applicationId.toString())
        );
        logger.info("Fetched application successfully for ID: {}", applicationId);
        return converter(application);
    }

    @Override
    @Cacheable(value = "applicationData",key = "'applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getAllApplications(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                getPaginatedApplications(applicationRepository.findAll(getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} applications", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'applications_recruiter_id'+#recruiterId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getAllApplicationsByRecruiter(Integer recruiterId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications By Recruiter- page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                getPaginatedApplications(applicationRepository.findRecruiterApplications(recruiterId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} applications By Recruiter", response.getData().size());
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
        Long count = applicationRepository.count();
        logger.info("Total applications count: {}", count);
        return count;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'candidate_'+#candidateId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getCandidateApplications(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications Of candidateId : {}  - page: {}, size: {}, sortBy: {}, sortDir: {}",candidateId, page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                getPaginatedApplications(applicationRepository.findByCandidateCandidateId(candidateId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} applications of candidateId : {}", response.getData().size(),candidateId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'position_'+#positionId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getPositionApplications(Integer positionId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all applications Of positionId : {}  - page: {}, size: {}, sortBy: {}, sortDir: {}",positionId, page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response = getPaginatedApplications(
                applicationRepository.findByPositionPositionId(positionId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} applications of positionId : {}", response.getData().size(),positionId);
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'reviewer_'+#reviewerId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getAllShortlistedApplicationsByReviewer(Integer reviewerId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Shortlisted applications By Reviewer - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                getPaginatedApplications(applicationRepository.findShortlistsByReviewer(reviewerId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} Shortlisted applications By Reviewer", response.getData().size());
        return response;
    }

    @Override
    @Cacheable(value = "applicationData",key = "'reviewer_'+#reviewerId+'_positionId_'+#positionId+'_applications_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<ApplicationResponseDto> getPositionShortlistedApplicationsByReviewer(Integer positionId, Integer reviewerId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Positions Shortlisted applications By Reviewer - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        PaginatedResponse<ApplicationResponseDto> response =
                getPaginatedApplications(applicationRepository.findByShortlistedByPositionAndReviewer(positionId,reviewerId,getPageable(page, size, sortBy, sortDir)));
        logger.info("Fetched {} Shortlisted applications By Position And Reviewer", response.getData().size());
        return response;
    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PaginatedResponse<ApplicationResponseDto> getPaginatedApplications(Page<ApplicationModel> pageResponse) {
        PaginatedResponse<ApplicationResponseDto> response = new PaginatedResponse<>();
        response.setData(convertContent(pageResponse.getContent()));
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        return response;
    }

    private List<ApplicationResponseDto> convertContent(List<ApplicationModel> content) {
        List<ApplicationResponseDto> applications = new ArrayList<>();
        content.forEach(application -> applications.add(converter(application)));
        return applications;
    }

    protected ApplicationResponseDto converter(ApplicationModel entity){
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setApplicationId(entity.getApplicationId());
        dto.setIsShortlisted(entity.getIsShortlisted());
        dto.setPositionId(entity.getPosition().getPositionId());
        dto.setMatchingScore(entity.getMatchScore());
        dto.setCandidateId(entity.getCandidate().getCandidateId());
        dto.setApplicationStatus(converter(entity.getApplicationStatus()));
        if(entity.getIsShortlisted() == true){
            dto.setShortlistedBy(converter(entity.getShortlistedBy()));
            dto.setApplicationRounds(entity.getRounds().stream().map(this::converter).toList());
        }
        return dto;
    }

    private ApplicationStatusResponseDto converter(ApplicationStatusModel entity){
        ApplicationStatusResponseDto dto = new ApplicationStatusResponseDto();
        dto.setApplicationStatusId(entity.getApplicationStatusId());
        dto.setApplicationStatus(entity.getApplicationStatus());
        dto.setApplicationFeedback(entity.getApplicationFeedback());
        return dto;
    }

    private RoundResponseDto converter(RoundModel entity){
        RoundResponseDto dto = new RoundResponseDto();
        dto.setRoundId(entity.getRoundId());
        dto.setRoundType(entity.getRoundType());
        dto.setRoundResult(entity.getRoundResult());
        dto.setRoundDate(entity.getRoundDate());
        dto.setRoundExpectedTime(entity.getRoundExpectedTime());
        dto.setRoundDurationInMinutes(entity.getRoundDurationInMinutes());
        dto.setRoundSequence(entity.getRoundSequence());
        dto.setRoundFeedback(entity.getRoundFeedback());
        dto.setRoundRating(entity.getRoundRating());
        return dto;
    }

    private InterviewResponseDto converter(InterviewModel entity) {
        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setInterviewId(entity.getInterviewId());
        dto.setRoundId(entity.getRound().getRoundId());

        dto.setCandidateId(entity.getRound().getApplication().getCandidate().getCandidateId());
        dto.setPositionId(entity.getRound().getApplication().getPosition().getPositionId());
        dto.setApplicationId(entity.getRound().getApplication().getApplicationId());

        dto.setInterviewLink(entity.getInterviewLink());
        dto.setInterviewTime(entity.getInterviewTime());
        dto.setInterviewEndTime(entity.getInterviewEndTime());
        dto.setInterviewDate(entity.getInterviewDate());
        dto.setInterviewStatus(entity.getInterviewStatus());
        dto.setInterviewers(entity.getInterviewers().stream().map(this::converter).collect(Collectors.toSet()));
        logger.debug("Converted InterviewModel (id={}) to DTO", entity.getInterviewId());
        return dto;
    }

    private InterviewInterviewerResponseDto converter(InterviewInterviewerModel entity) {
        InterviewInterviewerResponseDto dto = new InterviewInterviewerResponseDto();
        dto.setInterviewInterviewerId(entity.getInterviewInterviewerId());
        dto.setInterviewer(converter(entity.getInterviewer()));
        dto.setInterviewerFeedback(converter(entity.getInterviewerFeedback()));
        return dto;
    }


    private UserMinimalResponseDto converter(UserModel entity) {
        if(entity == null)
            return null;
        logger.trace("Mapping UserModel -> UserResponseDto for ID: {}", entity.getUserId());
        UserMinimalResponseDto userResponseDto = new UserMinimalResponseDto();
        userResponseDto.setUserId(entity.getUserId());
        userResponseDto.setUserName(entity.getUsername());
        userResponseDto.setUserEmail(entity.getUserEmail());
        userResponseDto.setUserImageUrl(entity.getUserImageUrl());
        return userResponseDto;
    }

    private InterviewerFeedbackResponseDto converter(InterviewerFeedbackModel interviewer) {
        if(interviewer == null)
            return null;
        InterviewerFeedbackResponseDto dto = new InterviewerFeedbackResponseDto();
        dto.setInterviewFeedbackId(interviewer.getInterviewFeedbackId());
        dto.setInterviewFeedback(interviewer.getInterviewFeedback());
        dto.setSkillRatings(interviewer.getSkillRatings().stream().map(this::convertor).toList());
        return dto;
    }

    private SkillRatingResponseDto convertor(SkillRatingModel s) {
        if(s == null)
            return null;
        SkillRatingResponseDto dto = new SkillRatingResponseDto();
        dto.setSkillRatingId(s.getSkillRatingId());
        dto.setSkillRating(s.getSkillRating());
        dto.setSkillFeedback(s.getSkillFeedback());
        dto.setSkill(convertor(s.getSkill()));
        return dto;
    }

    private SkillResponseDto convertor(SkillModel entity) {
        SkillResponseDto dto = new SkillResponseDto();

        dto.setSkillId(entity.getSkillId());
        dto.setSkill(entity.getSkill());

        return dto;
    }
}
