package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.ApplicationStatusDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface ApplicationServiceInterface {

    ApplicationDto addApplication(ApplicationDto newApplication);

    ApplicationDto updateApplication(Integer applicationId,ApplicationDto newApplication);

    ApplicationStatusDto updateApplicationStatus(Integer applicationId,Integer applicationStatusId, ApplicationStatusDto newApplicationStatus);

    ApplicationDto getApplication(Integer applicationId);

    PaginatedResponse<ApplicationDto> getAllApplications(Integer page,Integer size,String sortBy,String sortDir);

    List<Integer> getCandidateApplicationId(Integer candidateId);

    void deleteApplication(Integer applicationId);

    Long countTotalApplications();

    PaginatedResponse<ApplicationDto> getCandidateApplications(Integer candidateId,Integer page,Integer size,String sortBy,String sortDir);

    PaginatedResponse<ApplicationDto> getPositionApplications(Integer positionId,Integer page,Integer size,String sortBy,String sortDir);
}
