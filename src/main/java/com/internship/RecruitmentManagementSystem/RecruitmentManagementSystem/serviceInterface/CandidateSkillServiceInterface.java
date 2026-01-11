package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.skill.CandidateSkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.skill.CandidateSkillUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateSkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface CandidateSkillServiceInterface {

    CandidateSkillResponseDto addCandidateSKill(CandidateSkillCreateDto candidateSkill);
    CandidateSkillResponseDto getCandidateSkillById(Integer candidateSkillId);
    PaginatedResponse<CandidateSkillResponseDto> getAllData(Integer page, Integer size, String sortBy, String sortDir);
    void deleteCandidateSkill(Integer candidateSKillId);
    PaginatedResponse<CandidateSkillResponseDto> getCandidateSKillByCandidateId(Integer candidateId,Integer page,Integer size,String sortBy,String sortDir);
    PaginatedResponse<CandidateSkillResponseDto> getAllCandidatesBySkillId(Integer skillId,Integer page,Integer size,String sortBy,String sortDir);
    CandidateSkillResponseDto updateCandidateSkill(Integer candidateSkillId, CandidateSkillUpdateDto CandidateSkill);

}
