package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.degree.DegreeResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface DegreeServiceInterface {

    DegreeResponseDto addDegree(DegreeCreateDto degreeDto);

    void deleteDegree(Integer degreeId);

    DegreeResponseDto getDegree(Integer degreeId);

    DegreeResponseDto updateDegree(Integer degreeId, DegreeUpdateDto degreeDto);

    PaginatedResponse<DegreeResponseDto> getAllDegrees(int page, int size, String sortBy, String sortDir);
}
