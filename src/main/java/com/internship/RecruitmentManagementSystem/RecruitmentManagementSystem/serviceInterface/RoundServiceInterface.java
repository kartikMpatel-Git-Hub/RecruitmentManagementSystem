package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.RoundCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.RoundStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.RoundUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.RoundStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface RoundServiceInterface {

    RoundResponseDto addRound(Integer applicationId, RoundCreateDto roundDto);

    void removeRound(Integer roundId);

    RoundResponseDto getRound(Integer roundId);

    RoundResponseDto updateRound(Integer roundId, RoundUpdateDto round);

    RoundStatusResponseDto updateRoundStatus(Integer roundId, Integer roundStatusId, RoundStatusUpdateDto roundStatus);

    PaginatedResponse<RoundResponseDto> applicationRound(Integer applicationId, Integer page, Integer size, String sortBy, String sortDir);

    PaginatedResponse<RoundResponseDto> candidateRounds(Integer candidateId,Integer page,Integer size,String sortBy,String sortDir);

}
