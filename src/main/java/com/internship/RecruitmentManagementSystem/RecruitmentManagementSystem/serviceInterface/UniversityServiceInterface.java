package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UniversityDto;

import java.util.List;

public interface UniversityServiceInterface {
    public UniversityDto addUniversity(UniversityDto university);
    public UniversityDto getUniversityByName(String universityName);
    public UniversityDto getUniversityById(Integer universityId);

    public List<UniversityDto> getAllUniversities();

    public UniversityDto updateUniversity(Integer universityId, UniversityDto universityDto);
    public void deleteUniversity(Integer universityId);

}
