package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewerFeedbackCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewerFeedbackUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewInterviewerResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewerFeedbackResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillRatingResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserMinimalResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.InterviewServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.HtmlTemplateBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService implements InterviewServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(InterviewService.class);

    private final InterviewRepository interviewRepository;
    private final InterviewInterviewerRepository interviewInterviewerRepository;
    private final InterviewerFeedbackRepository interviewerFeedbackRepository;
    private final SkillRepository skillRepository;
    private final SkillRatingRepository skillRatingRepository;
    private final RoundRepository roundRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final HtmlTemplateBuilder templateBuilder;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "applicationData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public Boolean createInterview(InterviewCreateDto dto) {
        logger.info("Creating new interview for roundId={}", dto.getRoundId());
        String candidateName,interviewDate,interviewTime,jobRole,link,candidateEmail;
        StringBuilder interviewerList = new StringBuilder();
        RoundModel round = roundRepository.findById(dto.getRoundId())
                .orElseThrow(() -> {
                    logger.error("Round not found with roundId={}", dto.getRoundId());
                    return new ResourceNotFoundException("Round", "roundId", dto.getRoundId().toString());
                });
        interviewDate = dto.getInterviewDate().toString();
        interviewTime = dto.getInterviewTime().toString();
        link = dto.getInterviewLink();
        candidateName = round.getApplication().getCandidate().getUser().getUsername();
        candidateEmail = round.getApplication().getCandidate().getUser().getUserEmail();
        jobRole = round.getApplication().getPosition().getPositionTitle();

        logger.info("Round Founded !!");
        InterviewModel interview = new InterviewModel();
        interview.setRound(round);
        interview.setInterviewLink(dto.getInterviewLink());
        interview.setInterviewTime(dto.getInterviewTime());
        interview.setInterviewEndTime(dto.getInterviewEndTime());
        interview.setInterviewDate(dto.getInterviewDate());
        interview.setInterviewStatus(InterviewStatus.SCHEDULED);

        Integer duration = (int)Duration.between(dto.getInterviewTime(),dto.getInterviewEndTime()).toMinutes();

        round.setRoundDate(dto.getInterviewDate());
        round.setRoundExpectedTime(dto.getInterviewTime());

        round.setRoundDurationInMinutes(duration);

        roundRepository.save(round);

        InterviewModel savedInterview = interviewRepository.save(interview);
        logger.info("Interview created successfully with interviewId={} for roundId={}", savedInterview.getInterviewId(), dto.getRoundId());

        // Assign interviewers if present
        if (
                dto.getNumberOfInterviewers() != null && dto.getNumberOfInterviewers() > 0
//                dto.getInterviewerIds() != null && !dto.getInterviewerIds().isEmpty()
        ) {
//            logger.debug("Assigning {} interviewers to interviewId={}", dto.getInterviewerIds().size(), savedInterview.getInterviewId());
//            Set<InterviewInterviewerModel> interviewers = dto.getInterviewerIds().stream().map(id -> {
//                UserModel interviewer = userRepository.findById(id)
//                        .orElseThrow(() -> {
//                            logger.error("Interviewer not found with userId={}", id);
//                            return new ResourceNotFoundException("User", "userId", id.toString());
//                        });
//                interviewerList.append(interviewer.getUsername()).append(", ");
//                InterviewInterviewerModel model = new InterviewInterviewerModel();
//                model.setInterview(savedInterview);
//                boolean isSkillRating = round.getRoundType().equals(RoundType.TECHNICAL);
//                model.setInterviewerFeedback(createInterviewFeedback(round.getApplication().getPosition().getPositionRequirements(),isSkillRating));
//                model.setInterviewer(interviewer);
//                return model;
//            }).collect(Collectors.toSet());

            List<UserModel> freeInterviewers;
            if(round.getRoundType().equals(RoundType.TECHNICAL)){
                freeInterviewers = interviewInterviewerRepository.findFreeInterviewers(
                        dto.getInterviewDate(),
                        dto.getInterviewTime(),
                        dto.getInterviewEndTime()
                );
            }else{
                freeInterviewers = interviewInterviewerRepository.findFreeHrs(
                        dto.getInterviewDate(),
                        dto.getInterviewTime(),
                        dto.getInterviewEndTime()
                );
            }

            if(freeInterviewers.size() < dto.getNumberOfInterviewers()){
                logger.error("Not enough free interviewers available. Required: {}, Available: {}", dto.getNumberOfInterviewers(), freeInterviewers.size());
                throw new FailedProcessException("Not enough free interviewers available.");
            }

            List<UserModel> selectedInterviewers =
                    pickLeastBusyInterviewers(freeInterviewers, dto.getNumberOfInterviewers());

            Set<InterviewInterviewerModel> interviewers = selectedInterviewers.stream().map(interviewer -> {
                interviewerList.append(interviewer.getUsername()).append(", ");
                InterviewInterviewerModel model = new InterviewInterviewerModel();
                model.setInterview(savedInterview);
                boolean isSkillRating = round.getRoundType().equals(RoundType.TECHNICAL);
                model.setInterviewerFeedback(createInterviewFeedback(round.getApplication().getPosition().getPositionRequirements(),isSkillRating));
                model.setInterviewer(interviewer);
                model.setIsFeedbackGiven(false);
                return model;
            }).collect(Collectors.toSet());

            interviewInterviewerRepository.saveAll(interviewers);
            savedInterview.getInterviewers().addAll(interviewers);
            interviewers.forEach(interviewer ->{
                emailService.mailToInterviewer(interviewer.getInterviewer().getUsername(),
                        interviewer.getInterviewer().getUserEmail(),
                        candidateName,
                        interviewDate,
                        interviewTime,
                        jobRole,
                        link,
                        interviewerList.toString()
                );
            });
            emailService.mailToCandidate(
                    candidateName,
                    candidateEmail,
                    interviewDate,
                    interviewTime,
                    interviewerList.toString(),
                    jobRole,
                    link
            );
            logger.info("Assigned {} interviewers successfully to interviewId={}", interviewers.size(), savedInterview.getInterviewId());
        }
        return true;
    }

    private InterviewerFeedbackModel createInterviewFeedback(List<PositionRequirementModel> positionRequirements,boolean isSkillRating) {
        InterviewerFeedbackModel interviewerFeedback = new InterviewerFeedbackModel();
        interviewerFeedback.setInterviewFeedback("");
        InterviewerFeedbackModel savedInterviewerFeedback = interviewerFeedbackRepository.save(interviewerFeedback);

        if(isSkillRating){
            List<SkillRatingModel> ratings = new ArrayList<>();
            positionRequirements.forEach(positionRequirement -> {
                logger.info(positionRequirement.getPositionRequirementId().toString());
                SkillRatingModel skillRatingModel = new SkillRatingModel();
                skillRatingModel.setSkillRating(0D);
                skillRatingModel.setSkillFeedback("");
                SkillModel skill = skillRepository.findById(positionRequirement.getPositionRequiredSkill().getSkillId()).orElseThrow(
                        ()->new ResourceNotFoundException("Skill","skillId",positionRequirement.getPositionRequiredSkill().getSkillId().toString())
                );
                skillRatingModel.setSkill(skill);
                skillRatingModel.setFeedback(savedInterviewerFeedback);
                ratings.add(skillRatingRepository.save(skillRatingModel));
            });
            savedInterviewerFeedback.setSkillRatings(ratings);
        }
        return savedInterviewerFeedback;
    }


    @Override
    @Cacheable(value = "interviewData", key = "'interviewId-'+#interviewId")
    public InterviewResponseDto getInterview(Integer interviewId) {
        InterviewModel existingInterview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> {
                    logger.error("Interview not found with interviewId = {}", interviewId);
                    return new ResourceNotFoundException("Interview", "interviewId", interviewId.toString());
                });
        return converter(existingInterview);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public InterviewResponseDto interviewComplete(Integer interviewId) {
        logger.info("interviewId={} Fetching !",interviewId);
        InterviewModel existingInterview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> {
                    logger.error("Interview not found with interviewId = {}", interviewId);
                    return new ResourceNotFoundException("Interview", "interviewId", interviewId.toString());
                });
        logger.info("interviewId={} Get !",interviewId);
        existingInterview.setInterviewStatus(InterviewStatus.COMPLETED);
        var completedInterview = interviewRepository.save(existingInterview);
        logger.info("interviewId={} Completed !",interviewId);
        return converter(completedInterview);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public void deleteInterview(Integer interviewId) {
        InterviewModel existingInterview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> {
                    logger.error("Interview not found with interviewId = {}", interviewId);
                    return new ResourceNotFoundException("Interview", "interviewId", interviewId.toString());
                });
        interviewRepository.delete(existingInterview);
        logger.info("Interview deleted successfully with interviewId={}", interviewId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true),
            @CacheEvict(value = "applicationData", allEntries = true)
    })
    public InterviewResponseDto updateInterview(Integer interviewId, InterviewUpdateDto dto) {
        InterviewModel existingInterview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> {
                    logger.error("Interview not found with interviewId = {}", interviewId);
                    return new ResourceNotFoundException("Interview", "interviewId", interviewId.toString());
                });
        existingInterview.setInterviewLink(dto.getInterviewLink());
        existingInterview.setInterviewDate(dto.getInterviewDate());
        existingInterview.setInterviewEndTime(dto.getInterviewEndTime());
        existingInterview.setInterviewTime(dto.getInterviewTime());
        existingInterview.setInterviewStatus(dto.getInterviewStatus());

        RoundModel round = existingInterview.getRound();
        round.setRoundDate(dto.getInterviewDate());
        round.setRoundExpectedTime(dto.getInterviewTime());
        Integer duration =(int)Duration.between(dto.getInterviewTime(),dto.getInterviewEndTime()).toMinutes();
        round.setRoundDurationInMinutes(duration);
        roundRepository.save(round);
        InterviewModel updatedInterview = interviewRepository.save(existingInterview);

        return converter(updatedInterview);
    }

    @Override
    @Cacheable(value = "interviewData", key = "'round'+#roundId + '-' + #page + '-' + #size + '-' + #sortBy + '-' + #sortDir")
    public List<InterviewResponseDto> getInterviewsByRound(Integer roundId) {
        logger.info("Fetching interviews for roundId={}", roundId);
        List<InterviewModel> interviews = interviewRepository.findByRoundRoundId(roundId);
        logger.info("Fetched {} interviews for roundId={}", interviews.size(), roundId);
        return interviews.stream().map(this::converter).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "interviewData", key = "'all'+ '-' + #page + '-' + #size + '-' + #sortBy + '-' + #sortDir")
    public PaginatedResponse<InterviewResponseDto> getAllInterviews(Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all interviews -> page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Page<InterviewModel> pageResult = interviewRepository.findAll(getPageable(page, size, sortBy, sortDir));
        logger.info("Fetched {} total interviews", pageResult.getTotalElements());
        return getPaginatedInterview(pageResult);
    }

    @Override
    @Cacheable(value = "interviewData", key = "'candidate'+#candidateId + '-' + #page + '-' + #size + '-' + #sortBy + '-' + #sortDir")
    public PaginatedResponse<InterviewResponseDto> getCandidateInterviews(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching candidate interviews for candidateId={} -> page={}, size={}", candidateId, page, size);
        Page<InterviewModel> pageResult = interviewRepository.findInterviewsByCandidateId(candidateId, getPageable(page, size, sortBy, sortDir));
        logger.info("Fetched {} interviews for candidateId={}", pageResult.getTotalElements(), candidateId);
        return getPaginatedInterview(pageResult);
    }

    @Override
    @Cacheable(value = "interviewData", key = "'interviewer'+#interviewerId + '-' + #page + '-' + #size + '-' + #sortBy + '-' + #sortDir")
    public PaginatedResponse<InterviewResponseDto> getInterviewerInterviews(Integer interviewerId, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching interviews for interviewerId={} -> page={}, size={}", interviewerId, page, size);
        Page<InterviewModel> pageResult = interviewRepository.findByInterviewerId(interviewerId, getPageable(page, size, sortBy, sortDir));
        logger.info("Fetched {} interviews for interviewerId={}", pageResult.getTotalElements(), interviewerId);
        return getPaginatedInterview(pageResult);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public InterviewerFeedbackResponseDto addFeedbackToInterview(Integer interviewInterviewerId, InterviewerFeedbackCreateDto feedbackCreateDto) {
        InterviewInterviewerModel interviewerModel = interviewInterviewerRepository.findById(interviewInterviewerId)
                .orElseThrow(() -> {
                    logger.error("InterviewInterviewer not found with interviewInterviewerId={}", interviewInterviewerId);
                    return new ResourceNotFoundException("InterviewInterviewer", "interviewInterviewerId", interviewInterviewerId.toString());
                });
        InterviewerFeedbackModel interviewerFeedbackModel = new InterviewerFeedbackModel();
        interviewerFeedbackModel.setInterviewFeedback(feedbackCreateDto.getInterviewFeedback());
        interviewerFeedbackModel.setSkillRatings(feedbackCreateDto.getSkillRatings().stream().map(
                skillRatingCreateDto -> {
                    SkillRatingModel skillRatingModel = new SkillRatingModel();
                    SkillModel skillModel = new SkillModel();
                    skillModel.setSkillId(skillRatingCreateDto.getSkillId());
                    skillRatingModel.setSkill(skillModel);
                    skillRatingModel.setSkillRating(skillRatingCreateDto.getSkillRating());
                    skillRatingModel.setSkillFeedback(skillRatingCreateDto.getSkillFeedback());
                    skillRatingModel.setFeedback(interviewerFeedbackModel);
                    return skillRatingModel;
                }
        ).toList());
        interviewerModel.setInterviewerFeedback(interviewerFeedbackModel);
        interviewerModel.setIsFeedbackGiven(true);
        var savedInterviewerFeedback = interviewInterviewerRepository.save(interviewerModel);
        logger.info("Added feedback for interviewInterviewerId={}", interviewInterviewerId);
        return converter(savedInterviewerFeedback.getInterviewerFeedback());
    }

    @Override
    public InterviewerFeedbackResponseDto getFeedbackById(Integer feedbackId) {
        InterviewerFeedbackModel interviewerFeedback = interviewerFeedbackRepository.findById(feedbackId).orElseThrow(
                ()-> {
                    logger.error("InterviewerFeedback not found with feedbackId={}", feedbackId);
                    return new ResourceNotFoundException("InterviewerFeedback", "feedbackId", feedbackId.toString());
                }
        );
        return converter(interviewerFeedback);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public InterviewerFeedbackResponseDto updateFeedbackById(Integer interviewId,Integer feedbackId, InterviewerFeedbackUpdateDto updatingFeedback) {
        logger.info("Updating feedback for feedbackId={}", feedbackId);

        InterviewerFeedbackModel interviewerFeedback = interviewerFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> {
                    logger.error("Feedback not found with feedbackId={}", feedbackId);
                    return new ResourceNotFoundException("InterviewFeedback", "interviewFeedbackId", feedbackId.toString());
                });
        InterviewInterviewerModel interviewerInterview = interviewInterviewerRepository.findByInterviewerFeedbackId(feedbackId).orElseThrow(
                ()->{
                    logger.error("Interview not found with feedbackId={}", feedbackId);
                    return new ResourceNotFoundException("InterviewInterviewerFeedback", "feedbackId", feedbackId.toString());
                }
        );
        logger.info("Updating feedback text for feedbackId={}", feedbackId);
        interviewerFeedback.setInterviewFeedback(updatingFeedback.getInterviewFeedback());

        logger.info("Updating skill ratings for feedbackId={}", feedbackId);
        logger.info("Updating skill ratings total skills={}", updatingFeedback.getSkillRatings().size());
        updatingFeedback.getSkillRatings().forEach(skillRating -> {
            logger.info("Updating Skill ratings with skillRatingId={}",skillRating.getSkillRatingId());
            SkillRatingModel skill = skillRatingRepository.findById(skillRating.getSkillRatingId())
                    .orElseThrow(() -> new ResourceNotFoundException("SkillRating", "skillRatingId", skillRating.toString()));
            skill.setSkillFeedback(skillRating.getSkillFeedback());
            skill.setSkillRating(skillRating.getSkillRating());
            skillRatingRepository.save(skill);
        });

        InterviewerFeedbackModel updatedInterviewerFeedback = interviewerFeedbackRepository.save(interviewerFeedback);
        interviewerInterview.setIsFeedbackGiven(true);
        interviewInterviewerRepository.save(interviewerInterview);
        logger.info("Updated feedback successfully for feedbackId={}", feedbackId);
        return converter(updatedInterviewerFeedback);
    }


    @Override
    public InterviewerFeedbackResponseDto getInterviewerFeedback(Integer interviewId, Integer interviewerId) {
        InterviewerFeedbackModel interviewerFeedback = interviewerFeedbackRepository.findInterviewerFeedback(interviewId,interviewerId).orElseThrow(
                ()-> {
                    logger.error("InterviewerFeedback not found with interviewId={} and interviewerId={}", interviewId,interviewerId);
                    return new ResourceNotFoundException("InterviewerFeedback", "interviewId and interviewerId", interviewId.toString()+","+interviewerId.toString());
                }
        );
        return converter(interviewerFeedback);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public void deleteFeedback(Integer feedbackId) {
        InterviewerFeedbackModel interviewerFeedback = interviewerFeedbackRepository.findById(feedbackId).orElseThrow(
                ()-> {
                    logger.error("InterviewerFeedback not found with feedbackId={}", feedbackId);
                    return new ResourceNotFoundException("InterviewerFeedback", "feedbackId", feedbackId.toString());
                }
        );
        interviewerFeedbackRepository.delete(interviewerFeedback);
        logger.info("Deleted feedback with feedbackId={}", feedbackId);
    }

    private List<UserModel> pickLeastBusyInterviewers(
            List<UserModel> freeInterviewers,
            Integer count
    ) {
        if (freeInterviewers.isEmpty()) {
            throw new RuntimeException("No free interviewers available!");
        }
        List<Object[]> loadList = interviewInterviewerRepository.getInterviewerLoad();
        Map<Integer, Long> loadMap = loadList.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));
        List<UserModel> sorted = freeInterviewers.stream()
                .sorted(Comparator.comparing(i ->
                        loadMap.getOrDefault(i.getUserId(), 0L)
                ))
                .toList();

        long lowestLoad = loadMap.getOrDefault(sorted.get(0).getUserId(), 0L);

        List<UserModel> lowestLoaded = sorted.stream()
                .filter(i -> loadMap.getOrDefault(i.getUserId(), 0L) == lowestLoad)
                .toList();

        List<UserModel> mutableList = new ArrayList<>(lowestLoaded);

        Collections.shuffle(mutableList);

        return mutableList.stream().limit(count).toList();
    }


    private PaginatedResponse<InterviewResponseDto> getPaginatedInterview(Page<InterviewModel> pageResponse) {
        logger.debug("Building paginated interview response for {} records", pageResponse.getContent().size());
        PaginatedResponse<InterviewResponseDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.getContent().stream().map(this::converter).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        logger.info("Paginated interview response built successfully: page {}/{}", pageResponse.getNumber() + 1, pageResponse.getTotalPages());
        return response;
    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Creating pageable with sortBy={}, sortDir={}", sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
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
        dto.setIsFeedbackGiven(entity.getIsFeedbackGiven() != null ? entity.getIsFeedbackGiven() : false);
        dto.setInterviewerFeedback(converter(entity.getInterviewerFeedback()));
        return dto;
    }

    private UserMinimalResponseDto converter(UserModel entity) {
        logger.trace("Mapping UserModel -> UserResponseDto for ID: {}", entity.getUserId());
        UserMinimalResponseDto userResponseDto = new UserMinimalResponseDto();
        userResponseDto.setUserId(entity.getUserId());
        userResponseDto.setUsername(entity.getUsername());
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
