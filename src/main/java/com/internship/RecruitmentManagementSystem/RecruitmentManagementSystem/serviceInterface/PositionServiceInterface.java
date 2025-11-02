package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.DegreeDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.PositionDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PositionServiceInterface {

    PositionDto addPosition(PositionDto newPosition);

    PositionDto updatePosition(Integer positionId,PositionDto newPosition);

    PositionDto getPosition(Integer positionId);

    void deletePosition(Integer positionId);

    PositionDto changeEducation(Integer positionId, List<DegreeDto> positionEducation);

    PaginatedResponse<PositionDto> getAllPositions(Integer page, Integer size, String sortBy, String sortDir);

    Long countActivePosition();
}
