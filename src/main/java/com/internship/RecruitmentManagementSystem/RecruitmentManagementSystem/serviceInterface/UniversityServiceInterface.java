package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.university.UniversityCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.university.UniversityUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.university.UniversityResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface UniversityServiceInterface {
    UniversityResponseDto addUniversity(UniversityCreateDto university);
    UniversityResponseDto getUniversityByName(String universityName);
    UniversityResponseDto getUniversityById(Integer universityId);
    PaginatedResponse<UniversityResponseDto> getAllUniversities(int page, int size, String sortBy, String sortDir);
    UniversityResponseDto updateUniversity(Integer universityId, UniversityUpdateDto universityDto);
    void deleteUniversity(Integer universityId);

}
