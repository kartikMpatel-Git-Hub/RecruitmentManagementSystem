package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundResultDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundInterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoundModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.ApplicationRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoundRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.RoundServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoundService implements RoundServiceInterface {

    private final PaginatedResponseCreator paginatedResponseCreator;
    private final Mapper mapper;
    private final ModelServiceInterface modelService;

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public RoundResponseDto addRound(Integer applicationId, RoundCreateDto createDto) {
        log.info("Adding new round for applicationId={}", applicationId);
        ApplicationModel application = modelService.getApplication(applicationId);

        RoundModel newRound = getNewRound(createDto, application);

        RoundModel savedRound = modelService.addRound(newRound);
        log.info("Round added successfully with roundId={} for applicationId={}", savedRound.getRoundId(), applicationId);
        return mapper.toDto(savedRound, RoundResponseDto.class);
    }

    private static RoundModel getNewRound(RoundCreateDto createDto, ApplicationModel application) {
        RoundModel newRound = new RoundModel();
        newRound.setApplication(application);
        newRound.setRoundSequence(createDto.getRoundSequence());
        newRound.setRoundType(createDto.getRoundType());
        newRound.setRoundResult(RoundResult.PENDING);
        newRound.setRoundExpectedTime(createDto.getRoundExpectedTime());
        newRound.setRoundDate(createDto.getRoundDate());
        newRound.setRoundDurationInMinutes(createDto.getRoundDurationInMinutes());
        newRound.setRoundFeedback("");
        newRound.setRoundRating(0D);
        return newRound;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public void removeRound(Integer roundId) {
        log.info("Removing round with roundId={}", roundId);
        RoundModel round = modelService.getRound(roundId);
        modelService.deleteRound(round);
        log.info("Round deleted successfully with roundId={}", roundId);
    }

    @Override
    public RoundInterviewResponseDto getRound(Integer roundId) {
        log.info("Fetching round with roundId={}", roundId);
        RoundModel round = modelService.getRound(roundId);
        log.info("Round fetched successfully with roundId={}", roundId);
        return mapper.toDto(round,RoundInterviewResponseDto.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public RoundResponseDto roundResult(Integer roundId, RoundResultDto roundResult) {
        log.debug("Fetching round with roundId For Pass ={}", roundId);
        RoundModel round = modelService.getRound(roundId);
        round.setRoundResult(roundResult.getRoundResult());
        if(roundResult.getRoundResult().equals(RoundResult.FAIL)){
            ApplicationModel application = round.getApplication();
            application.getApplicationStatus().setApplicationStatus(ApplicationStatus.REJECTED);
            application.getRounds().forEach(r -> {
                if(round.getRoundResult().equals(RoundResult.PENDING)){
                    round.setRoundResult(RoundResult.CANCELLED);
                }
            });
            modelService.addApplication(application);
        }
        round.setRoundFeedback(roundResult.getRoundFeedback());
        round.setRoundRating(roundResult.getRoundRating());

        log.debug("Round Result Set successfully with roundId={}", roundId);
        RoundModel roundModel = modelService.addRound(round);
        return mapper.toDto(roundModel,RoundResponseDto.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public RoundResponseDto updateRound(Integer roundId, RoundUpdateDto round) {
        log.info("Updating round with roundId={}", roundId);
        RoundModel existingRound = modelService.getRound(roundId);
        if (round.getRoundExpectedDate() != null && round.getRoundExpectedTime() != null){
            existingRound.setRoundDate(round.getRoundExpectedDate());
            existingRound.setRoundExpectedTime(round.getRoundExpectedTime());
            ApplicationModel application = existingRound.getApplication();
            application.getApplicationStatus().setApplicationFeedback("Your Round Has Been Scheduled !");
            modelService.addApplication(application);
        }else{
            if (round.getRoundExpectedTime() != null){
                existingRound.setRoundExpectedTime(round.getRoundExpectedTime());
            }
            if (round.getRoundExpectedDate() != null){
                existingRound.setRoundDate(round.getRoundExpectedDate());
            }
        }

        if(round.getRoundDurationInMinutes() != null)
            existingRound.setRoundDurationInMinutes(round.getRoundDurationInMinutes());

        if (round.getRoundSequence() != null){
            manageSequence(existingRound.getRoundSequence(),round.getRoundSequence(),existingRound.getApplication().getRounds());
            existingRound.setRoundSequence(round.getRoundSequence());
        }
        RoundModel updatedRound = modelService.addRound(existingRound);
        log.info("Round updated successfully with roundId={}", roundId);
        return mapper.toDto(updatedRound, RoundResponseDto.class);
    }


    @Override
    @Cacheable(value = "roundData",key = "'rounds_application_'+#applicationId+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<RoundResponseDto> applicationRound(Integer applicationId, Integer page, Integer size, String sortBy, String sortDir) {
        log.debug("Fetching rounds for applicationId={} page={} size={}", applicationId, page, size);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getRoundsByApplicationId(applicationId, pageable),
                RoundResponseDto.class
        );
//                getPaginatedRounds(roundRepository.findByApplicationApplicationId(applicationId, getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Cacheable(value = "roundData",key = "'rounds_candidate_+'#candidateId'+_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<RoundResponseDto> candidateRounds(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        log.debug("Fetching rounds for candidateId={} page={} size={}", candidateId, page, size);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getRoundsByCandidateId(candidateId, pageable),
                RoundResponseDto.class
        );
//        return getPaginatedRounds(roundRepository.findByApplicationCandidateCandidateId(candidateId, getPageable(page, size, sortBy, sortDir)));
    }

    private void manageSequence(Integer oldSequence, Integer newSequence, List<RoundModel> rounds) {
        if (newSequence < oldSequence) {
            for (RoundModel round : rounds) {
                if (round.getRoundSequence() >= newSequence && round.getRoundSequence() < oldSequence) {
                    round.setRoundSequence(round.getRoundSequence() + 1); // shift down
                }
            }
        }
        else if(newSequence > oldSequence){
            for (RoundModel round : rounds) {
                if (round.getRoundSequence() <= newSequence && round.getRoundSequence() > oldSequence) {
                    round.setRoundSequence(round.getRoundSequence() - 1); // shift up
                }
            }
        }
    }
}
