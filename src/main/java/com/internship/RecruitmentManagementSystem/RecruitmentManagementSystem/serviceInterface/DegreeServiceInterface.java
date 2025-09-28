package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.DegreeDto;

import java.util.List;

public interface DegreeServiceInterface {

    public DegreeDto addDegree(DegreeDto degreeDto);

    public void deleteDegree(Integer degreeId);

    public DegreeDto getDegree(Integer degreeId);

    public DegreeDto updateDegree(Integer degreeId,DegreeDto degreeDto);

    public List<DegreeDto> getAllDegrees();
}
