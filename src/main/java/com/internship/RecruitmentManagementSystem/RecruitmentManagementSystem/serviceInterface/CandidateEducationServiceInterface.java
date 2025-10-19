package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateEducationDto;

import java.util.List;
import java.util.Optional;

public interface CandidateEducationServiceInterface {

    public CandidateEducationDto addCandidateEducation(CandidateEducationDto candidateEducationDto);
    public List<CandidateEducationDto> getAllCandidateEducations();
    public Optional<CandidateEducationDto> getCandidateEducationById(Integer candidateEducationId);
    public CandidateEducationDto updateCandidateEducation(Integer candidateEducationId, CandidateEducationDto candidateEducationDto);
    public void deleteCandidateEducation(Integer candidateEducationId);

    public List<CandidateEducationDto> getCandidateEducationByCandidateId(Integer candidateId);
    public List<CandidateEducationDto> getCandidateEducationByDegreeId(Integer degreeId);
    public List<CandidateEducationDto> getCandidateEducationByUniversityId(Integer universityId);

}
