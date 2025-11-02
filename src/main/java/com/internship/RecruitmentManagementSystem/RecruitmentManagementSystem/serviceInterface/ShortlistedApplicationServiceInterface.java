package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ShortlistedApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ShortlistedApplicationStatusDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface ShortlistedApplicationServiceInterface {

    ShortlistedApplicationDto shortListApplication(ShortlistedApplicationDto shortlistedApplication);

    ShortlistedApplicationStatusDto updateShortlistedApplicationStatus(Integer shortlistedApplicationStatusId, ShortlistedApplicationStatusDto shortlistedApplicationStatus);

    ShortlistedApplicationDto getShortlistedApplication(Integer shortlistedApplicationId);

    PaginatedResponse<ShortlistedApplicationDto> getAllShortlistedApplications(Integer page, Integer size, String sortBy, String sortDir);

    PaginatedResponse<ShortlistedApplicationDto> getCandidateShortlistedApplications(Integer candidateId, Integer page, Integer size, String sortBy, String sortDir);

    PaginatedResponse<ShortlistedApplicationDto> getPositionShortlistedApplications(Integer positionId,Integer page,Integer size,String sortBy,String sortDir);

}
