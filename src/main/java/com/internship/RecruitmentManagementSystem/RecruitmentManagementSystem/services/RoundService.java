package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.RoundDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.RoundStatusDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.RoundCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.RoundStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.RoundUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.RoundStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoundModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoundStatusModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.ApplicationRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoundRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoundStatusRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.RoundServiceInterface;
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

    private final ApplicationRepository applicationRepository;
    private final RoundRepository roundRepository;
    private final RoundStatusRepository roundStatusRepository;
    private final CandidateRepository candidateRepository;

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public RoundResponseDto addRound(Integer applicationId, RoundCreateDto createDto) {
        log.info("Adding new round for applicationId={}", applicationId);
        ApplicationModel application = applicationRepository.findById(applicationId).orElseThrow(() -> {
            log.error("Application not found for applicationId={}", applicationId);
            return new ResourceNotFoundException("Application", "applicationId", applicationId.toString());
        });

        RoundStatusModel roundStatus = new RoundStatusModel();
        roundStatus.setRating(null);
        roundStatus.setRoundFeedback("Your Round Will Schedule Soon !");
        roundStatus.setRoundStatus(RoundStatus.UNDERPROCESS);
        RoundStatusModel savedRoundStatus = roundStatusRepository.save(roundStatus);

        RoundModel newRound = getNewRound(createDto, application, savedRoundStatus);

        RoundModel savedRound = roundRepository.save(newRound);
        log.info("Round added successfully with roundId={} for applicationId={}", savedRound.getRoundId(), applicationId);
        return converter(savedRound);
    }

    private static RoundModel getNewRound(RoundCreateDto createDto, ApplicationModel application, RoundStatusModel savedRoundStatus) {
        RoundModel newRound = new RoundModel();
        newRound.setApplication(application);
        newRound.setRoundSequence(createDto.getRoundSequence());
        newRound.setRoundType(createDto.getRoundType());
        newRound.setRoundResult(RoundResult.PENDING);
        newRound.setRoundExpectedTime(createDto.getRoundExpectedTime());
        newRound.setRoundDate(createDto.getRoundDate());
        newRound.setRoundDurationInMinutes(createDto.getRoundDurationInMinutes());
        newRound.setRoundStatus(savedRoundStatus);
        return newRound;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public void removeRound(Integer roundId) {
        log.info("Removing round with roundId={}", roundId);
        RoundModel round = roundRepository.findById(roundId).orElseThrow(() -> {
            log.error("Round not found for roundId={}", roundId);
            return new ResourceNotFoundException("Round", "roundId", roundId.toString());
        });
        roundRepository.delete(round);
        log.info("Round deleted successfully with roundId={}", roundId);
    }

    @Override
    public RoundResponseDto getRound(Integer roundId) {
        log.debug("Fetching round with roundId={}", roundId);
        RoundModel round = roundRepository.findById(roundId).orElseThrow(() -> {
            log.error("Round not found for roundId={}", roundId);
            return new ResourceNotFoundException("Round", "roundId", roundId.toString());
        });
        log.debug("Round fetched successfully with roundId={}", roundId);
        return converter(round);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public RoundResponseDto updateRound(Integer roundId, RoundUpdateDto round) {
        log.info("Updating round with roundId={}", roundId);
        RoundModel existingRound = roundRepository.findById(roundId).orElseThrow(() -> {
            log.error("Round not found for roundId = {}", roundId);
            return new ResourceNotFoundException("Round", "roundId", roundId.toString());
        });
        if(round.getRoundStatus() != null){
//            if (round.getRoundStatus().getRating() != null)
//                existingRound.getRoundStatus().setRating(round.getRoundStatus().getRating());
            if (round.getRoundStatus().getRoundFeedback() != null)
                existingRound.getRoundStatus().setRoundFeedback(round.getRoundStatus().getRoundFeedback());
//            if (round.getRoundStatus().getRoundStatus() != null)
//                existingRound.getRoundStatus().setRoundStatus(round.getRoundStatus().getRoundStatus());
        }
        if (round.getRoundExpectedDate() != null && round.getRoundExpectedTime() != null){
            existingRound.setRoundDate(round.getRoundExpectedDate());
            existingRound.setRoundExpectedTime(round.getRoundExpectedTime());
            existingRound.getRoundStatus().setRoundStatus(RoundStatus.SCHEDULED);
            ApplicationModel application = existingRound.getApplication();
            application.getApplicationStatus().setApplicationFeedback("Your Round Has Been Scheduled !");
            applicationRepository.save(application);
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
//        if (round.getRoundType() != null) existingRound.setRoundType(round.getRoundType());
//
//        if (round.getRoundResult() != null){
//            existingRound.setRoundResult(round.getRoundResult());
//            ApplicationModel application = existingRound.getApplication();
//            if(round.getRoundResult().toString().equalsIgnoreCase("PASS") || round.getRoundResult().toString().equalsIgnoreCase("FAIL")){
//                existingRound.getRoundStatus().setRoundStatus(RoundStatus.COMPLETED);
//                if(round.getRoundResult().toString().equalsIgnoreCase("FAIL")){
//                    application.getApplicationStatus().setApplicationStatus(ApplicationStatus.REJECTED);
//                    application.getApplicationStatus().setApplicationFeedback("Unfortunately You Are Rejected " + round.getRoundStatus().getRoundFeedback());
//                }
//            }else{
//                application.getApplicationStatus().setApplicationStatus(ApplicationStatus.SHORTLISTED);
//                application.getApplicationStatus().setApplicationFeedback("Your Application is Shortlisted, Next Round Will Be In Process Soon !");
//            }
//            applicationRepository.save(application);
//        }
        RoundModel updatedRound = roundRepository.save(existingRound);
        log.info("Round updated successfully with roundId={}", roundId);
        return converter(updatedRound);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "applicationData",allEntries = true),
            @CacheEvict(value = "roundData",allEntries = true),
    })
    public RoundStatusResponseDto updateRoundStatus(Integer roundId,Integer roundStatusId, RoundStatusUpdateDto roundStatus) {
        log.info("Updating round status with roundStatusId={}", roundStatusId);
        RoundStatusModel existingRoundStatus = roundStatusRepository.findById(roundStatusId).orElseThrow(() -> {
            log.error("RoundStatus not found for roundStatusId={}", roundStatusId);
            return new ResourceNotFoundException("RoundStatus", "roundStatusId", roundStatusId.toString());
        });

        if (roundStatus.getRating() != null) existingRoundStatus.setRating(roundStatus.getRating());
        if (roundStatus.getRoundFeedback() != null) existingRoundStatus.setRoundFeedback(roundStatus.getRoundFeedback());
        if (roundStatus.getRoundStatus() != null) existingRoundStatus.setRoundStatus(roundStatus.getRoundStatus());

        RoundStatusModel updatedRoundStatus = roundStatusRepository.save(existingRoundStatus);
        log.info("Round status updated successfully for roundStatusId={}", roundStatusId);
        return convertor(updatedRoundStatus);
    }

    @Override
    @Cacheable(value = "roundData",key = "'rounds_application_+'#applicationId'+_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<RoundResponseDto> applicationRound(Integer applicationId, Integer page, Integer size, String sortBy, String sortDir) {
        log.debug("Fetching rounds for applicationId={} page={} size={}", applicationId, page, size);
        return getPaginatedRounds(roundRepository.findByApplicationApplicationId(applicationId, getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Cacheable(value = "roundData",key = "'rounds_candidate_+'#candidateId'+_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<RoundResponseDto> candidateRounds(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir) {
        log.debug("Fetching rounds for candidateId={} page={} size={}", candidateId, page, size);
        return getPaginatedRounds(roundRepository.findByApplicationCandidateCandidateId(candidateId, getPageable(page, size, sortBy, sortDir)));
    }

    private void manageSequence(Integer oldSequence, Integer newSequence, List<RoundModel> rounds) {
        if(newSequence.equals(oldSequence)){
            return;
        }
        else if (newSequence < oldSequence) {
            for (RoundModel round : rounds) {
                if (round.getRoundSequence() >= newSequence && round.getRoundSequence() < oldSequence) {
                    round.setRoundSequence(round.getRoundSequence() + 1); // shift down
                }
            }
        }
        else{
            for (RoundModel round : rounds) {
                if (round.getRoundSequence() <= newSequence && round.getRoundSequence() > oldSequence) {
                    round.setRoundSequence(round.getRoundSequence() - 1); // shift up
                }
            }
        }
    }

    private PaginatedResponse<RoundResponseDto> getPaginatedRounds(Page<RoundModel> pageResponse) {
        log.debug("Building paginated response: {} items found", pageResponse.getContent().size());
        PaginatedResponse<RoundResponseDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.getContent().stream().map(this::converter).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        log.info("Paginated response built successfully: page={} totalPages={}", pageResponse.getNumber(), pageResponse.getTotalPages());
        return response;
    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private RoundResponseDto converter(RoundModel entity) {
        RoundResponseDto dto = new RoundResponseDto();
        dto.setRoundId(entity.getRoundId());
        dto.setRoundType(entity.getRoundType());
        dto.setRoundResult(entity.getRoundResult());
        dto.setRoundDate(entity.getRoundDate());
        dto.setRoundExpectedTime(entity.getRoundExpectedTime());
        dto.setRoundDurationInMinutes(entity.getRoundDurationInMinutes());
        log.info("Duration : {}",entity.getRoundDurationInMinutes());
        dto.setRoundSequence(entity.getRoundSequence());
        dto.setRoundStatus(convertor(entity.getRoundStatus()));
        return dto;
    }

    private RoundStatusResponseDto convertor(RoundStatusModel entity) {
        RoundStatusResponseDto dto = new RoundStatusResponseDto();
        dto.setRoundStatusId(entity.getRoundStatusId());
        dto.setRating(entity.getRating());
        dto.setRoundStatus(entity.getRoundStatus());
        dto.setRoundFeedback(entity.getRoundFeedback());
        return dto;
    }
}
