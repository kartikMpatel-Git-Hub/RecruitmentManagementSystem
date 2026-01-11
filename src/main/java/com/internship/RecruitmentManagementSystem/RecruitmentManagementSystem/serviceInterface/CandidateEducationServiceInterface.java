package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.education.CandidateEducationCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.education.CandidateEducationUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateEducationResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;


public interface CandidateEducationServiceInterface {

    CandidateEducationResponseDto addCandidateEducation(CandidateEducationCreateDto candidateEducationDto);
    PaginatedResponse<CandidateEducationResponseDto> getAllCandidateEducations(int page, int size, String sortBy, String sortDir);
    CandidateEducationResponseDto getCandidateEducationById(Integer candidateEducationId);
    CandidateEducationResponseDto updateCandidateEducation(Integer candidateEducationId, CandidateEducationUpdateDto candidateEducationDto);
    void deleteCandidateEducation(Integer candidateEducationId);
    PaginatedResponse<CandidateEducationResponseDto> getCandidateEducationByCandidateId(Integer candidateId,int page,int size,String sortBy,String sortDir);
    PaginatedResponse<CandidateEducationResponseDto> getCandidateEducationByDegreeId(Integer candidateId,int page,int size,String sortBy,String sortDir);
    PaginatedResponse<CandidateEducationResponseDto> getCandidateEducationByUniversityId(Integer candidateId,int page,int size,String sortBy,String sortDir);

}
