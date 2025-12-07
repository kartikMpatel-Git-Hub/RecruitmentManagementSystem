package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundResultDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round.RoundUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface RoundServiceInterface {

    RoundResponseDto addRound(Integer applicationId, RoundCreateDto roundDto);

    void removeRound(Integer roundId);

    RoundResponseDto getRound(Integer roundId);

    RoundResponseDto roundResult(Integer roundId, RoundResultDto roundResult);

    RoundResponseDto updateRound(Integer roundId, RoundUpdateDto round);

    PaginatedResponse<RoundResponseDto> applicationRound(Integer applicationId, Integer page, Integer size, String sortBy, String sortDir);

    PaginatedResponse<RoundResponseDto> candidateRounds(Integer candidateId,Integer page,Integer size,String sortBy,String sortDir);

}
