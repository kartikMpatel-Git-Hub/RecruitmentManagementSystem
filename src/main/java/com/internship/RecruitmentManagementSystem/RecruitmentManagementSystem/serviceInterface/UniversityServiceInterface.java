package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UniversityDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UniversityServiceInterface {
    public UniversityDto addUniversity(UniversityDto university);
    public UniversityDto getUniversityByName(String universityName);
    public UniversityDto getUniversityById(Integer universityId);

    public PaginatedResponse<UniversityDto> getAllUniversities(int page, int size, String sortBy, String sortDir);

    public UniversityDto updateUniversity(Integer universityId, UniversityDto universityDto);
    public void deleteUniversity(Integer universityId);

}
