package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.DegreeDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface DegreeServiceInterface {

    public DegreeDto addDegree(DegreeDto degreeDto);

    public void deleteDegree(Integer degreeId);

    public DegreeDto getDegree(Integer degreeId);

    public DegreeDto updateDegree(Integer degreeId,DegreeDto degreeDto);

    public PaginatedResponse<DegreeDto> getAllDegrees(int page, int size, String sortBy, String sortDir);
}
