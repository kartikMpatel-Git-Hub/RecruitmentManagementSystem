package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateEducationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;
import java.util.Optional;

public interface CandidateEducationServiceInterface {

    public CandidateEducationDto addCandidateEducation(CandidateEducationDto candidateEducationDto);
//    public List<CandidateEducationDto> getAllCandidateEducations();
    public PaginatedResponse<CandidateEducationDto> getAllCandidateEducations(int page, int size, String sortBy, String sortDir);
    public Optional<CandidateEducationDto> getCandidateEducationById(Integer candidateEducationId);
    public CandidateEducationDto updateCandidateEducation(Integer candidateEducationId, CandidateEducationDto candidateEducationDto);
    public void deleteCandidateEducation(Integer candidateEducationId);

    public PaginatedResponse<CandidateEducationDto> getCandidateEducationByCandidateId(Integer candidateId,int page,int size,String sortBy,String sortDir);
    public PaginatedResponse<CandidateEducationDto> getCandidateEducationByDegreeId(Integer candidateId,int page,int size,String sortBy,String sortDir);
    public PaginatedResponse<CandidateEducationDto> getCandidateEducationByUniversityId(Integer candidateId,int page,int size,String sortBy,String sortDir);

}
