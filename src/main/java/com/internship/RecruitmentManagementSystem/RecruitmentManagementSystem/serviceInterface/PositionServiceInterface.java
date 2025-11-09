package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.PositionRequirementResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.PositionResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.PositionRoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface PositionServiceInterface {

    PositionResponseDto addPosition(PositionCreateDto newPosition);

    PositionResponseDto updatePosition(Integer positionId, PositionUpdateDto newPosition);

    PositionResponseDto getPosition(Integer positionId);

    void deletePosition(Integer positionId);

    PositionResponseDto changeEducation(Integer positionId, List<DegreeGetDto> positionEducation);

    PositionRoundResponseDto changeRound(Integer positionRoundId, PositionRoundUpdateDto positionRounds);

    void deleteRound(Integer positionRoundId);

    PositionRequirementResponseDto addPositionRequirement(Integer positionId, PositionRequirementCreateDto newPosition);

    PositionRequirementResponseDto updatePositionRequirement(Integer positionRequirementId,PositionRequirementUpdateDto newPosition);

    void removePositionRequirement(Integer positionRequirementId);

    PaginatedResponse<PositionRequirementResponseDto> getPositionRequirements(Integer positionId,Integer page,Integer size,String sortBY,String sortDir);

    PaginatedResponse<PositionResponseDto> getAllPositions(Integer page, Integer size, String sortBy, String sortDir);

    Long countActivePosition();

    PositionResponseDto addRound(Integer positionId, PositionRoundCreateDto positionRound);
}
