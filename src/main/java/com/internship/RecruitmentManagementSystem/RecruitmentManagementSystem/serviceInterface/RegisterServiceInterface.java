package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.register.RegisterUserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface RegisterServiceInterface {
    PaginatedResponse<RegisterUserResponseDto> getAllRequest(Integer page, Integer size, String sortBy, String sortDir);
    UserResponseDto acceptRequest(Integer registerId);
    void rejectRequest(Integer registerId);
    long countRequest();
}
