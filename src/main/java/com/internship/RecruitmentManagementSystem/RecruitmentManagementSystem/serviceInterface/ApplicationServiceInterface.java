package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application.ApplicationStatusUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application.ApplicationResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application.ApplicationStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface ApplicationServiceInterface {

    ApplicationResponseDto addApplication(ApplicationCreateDto newApplication);

    void shortlistApplication(Integer applicationId);

    PaginatedResponse<ApplicationResponseDto> getAllShortlistedApplications(Integer page,Integer size,String sortBy,String sortDir);

    PaginatedResponse<ApplicationResponseDto> getPositionShortlistedApplications(Integer positionId,Integer page,Integer size,String sortBy,String sortDir);

    PaginatedResponse<ApplicationResponseDto> getCandidateShortlistedApplications(Integer candidateId,Integer page,Integer size,String sortBy,String sortDir);

    ApplicationStatusResponseDto updateApplicationStatus(Integer applicationId, Integer applicationStatusId, ApplicationStatusUpdateDto newApplicationStatus);

    ApplicationResponseDto getApplication(Integer applicationId);

    PaginatedResponse<ApplicationResponseDto> getAllApplications(Integer page,Integer size,String sortBy,String sortDir);

    List<Integer> getCandidateApplicationId(Integer candidateId);

    void deleteApplication(Integer applicationId);

    Long countTotalApplications();

    PaginatedResponse<ApplicationResponseDto> getCandidateApplications(Integer candidateId,Integer page,Integer size,String sortBy,String sortDir);

    PaginatedResponse<ApplicationResponseDto> getPositionApplications(Integer positionId,Integer page,Integer size,String sortBy,String sortDir);
}
