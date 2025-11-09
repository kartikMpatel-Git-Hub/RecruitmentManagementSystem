package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.InterviewCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.InterviewUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.InterviewerFeedbackCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.InterviewServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService implements InterviewServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(InterviewService.class);

    private final InterviewRepository interviewRepository;
    private final InterviewInterviewerRepository interviewInterviewerRepository;
    private final InterviewerFeedbackRepository interviewerFeedbackRepository;
    private final RoundRepository roundRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interviewData", allEntries = true),
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public InterviewResponseDto createInterview(InterviewCreateDto dto) {
        logger.info("Creating new interview for roundId={}", dto.getRoundId());

        RoundModel round = roundRepository.findById(dto.getRoundId())
                .orElseThrow(() -> {
                    logger.error("Round not found with roundId={}", dto.getRoundId());
                    return new ResourceNotFoundException("Round", "roundId", dto.getRoundId().toString());
                });

        InterviewModel interview = new InterviewModel();
        interview.setRound(round);
        interview.setInterviewLink(dto.getInterviewLink());
        interview.setInterviewStatus(InterviewStatus.SCHEDULED);

        InterviewModel savedInterview = interviewRepository.save(interview);
        logger.info("Interview created successfully with interviewId={} for roundId={}", savedInterview.getInterviewId(), dto.getRoundId());

        // Assign interviewers if present
        if (dto.getInterviewerIds() != null && !dto.getInterviewerIds().isEmpty()) {
            logger.debug("Assigning {} interviewers to interviewId={}", dto.getInterviewerIds().size(), savedInterview.getInterviewId());
            Set<InterviewInterviewerModel> interviewers = dto.getInterviewerIds().stream().map(id -> {
                UserModel interviewer = userRepository.findById(id)
                        .orElseThrow(() -> {
                            logger.error("Interviewer not found with userId={}", id);
                            return new ResourceNotFoundException("User", "userId", id.toString());
                        });
                InterviewInterviewerModel model = new InterviewInterviewerModel();
                model.setInterview(savedInterview);
                model.setInterviewer(interviewer);
                return model;
            }).collect(Collectors.toSet());

            interviewInterviewerRepository.saveAll(interviewers);
            savedInterview.setInterviewers(interviewers);
            logger.info("Assigned {} interviewers successfully to interviewId={}", interviewers.size(), savedInterview.getInterviewId());
        }
        if(round.getRoundStatus().getRoundStatus() != RoundStatus.SCHEDULED){
            round.getRoundStatus().setRoundStatus(RoundStatus.SCHEDULED);
            roundRepository.save(round);
        }
        return converter(savedInterview);
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
            @CacheEvict(value = "roundData", allEntries = true)
    })
    public InterviewResponseDto updateInterview(Integer interviewId, InterviewUpdateDto dto) {
        InterviewModel existingInterview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> {
                    logger.error("Interview not found with interviewId = {}", interviewId);
                    return new ResourceNotFoundException("Interview", "interviewId", interviewId.toString());
                });
        existingInterview.getInterviewers().clear();
        if(dto.getInterviewerIds() != null){
            dto.getInterviewerIds().forEach(interviewer ->{
                UserModel newInterviewer = userRepository.findById(interviewer)
                        .orElseThrow(() -> {
                            logger.error("Interviewer not found with interviewerId={}", interviewer);
                            return new ResourceNotFoundException("Interviewer", "interviewerId", interviewer.toString());
                        });
                InterviewInterviewerModel interviewInterviewer = new InterviewInterviewerModel();
                interviewInterviewer.setInterview(existingInterview);
                interviewInterviewer.setInterviewer(newInterviewer);
                existingInterview.getInterviewers().add(interviewInterviewer);
            });
        }
        existingInterview.setInterviewLink(dto.getInterviewLink());

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
        dto.setInterviewLink(entity.getInterviewLink());
        dto.setInterviewTime(entity.getInterviewTime());
        dto.setLocalDate(entity.getLocalDate());
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
        logger.trace("Mapping UserModel -> UserResponseDto for ID: {}", entity.getUserId());
        UserMinimalResponseDto userResponseDto = new UserMinimalResponseDto();
        userResponseDto.setUserId(entity.getUserId());
        userResponseDto.setUserName(entity.getUsername());
        userResponseDto.setUserEmail(entity.getUserEmail());
        userResponseDto.setUserImageUrl(entity.getUserImageUrl());
        return userResponseDto;
    }

    private InterviewerFeedbackResponseDto converter(InterviewerFeedbackModel interviewer) {
        InterviewerFeedbackResponseDto dto = new InterviewerFeedbackResponseDto();
        dto.setInterviewFeedbackId(interviewer.getInterviewFeedbackId());
        dto.setInterviewFeedback(interviewer.getInterviewFeedback());
        dto.setSkillRatings(interviewer.getSkillRatings().stream().map(this::convertor).toList());
        return null;
    }

    private SkillRatingResponseDto convertor(SkillRatingModel s) {
        SkillRatingResponseDto dto = new SkillRatingResponseDto();
        dto.setSkillRatingId(s.getSkillRatingId());
        dto.setSkillRating(s.getSkillRating());
        dto.setSkillFeedback(s.getSkillFeedback());
        dto.setSkill(convertor(s.getSkill()));
        return null;
    }

    private SkillResponseDto convertor(SkillModel entity) {
        SkillResponseDto dto = new SkillResponseDto();

        dto.setSkillId(entity.getSkillId());
        dto.setSkill(entity.getSkill());

        return dto;
    }

}
