package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.PositionRequirementDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface PositionRequirementServiceInterface {

    PositionRequirementDto addPositionRequirement(Integer positionId,PositionRequirementDto newPosition);

    PositionRequirementDto updatePositionRequirement(Integer positionRequirementId,PositionRequirementDto newPosition);

    void removePositionRequirement(Integer positionRequirementId);

    PaginatedResponse<PositionRequirementDto> getPositionRequirements(Integer positionId,Integer page,Integer size,String sortBY,String sortDir);
}
