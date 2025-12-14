package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateSkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface CandidateSkillServiceInterface {

    public CandidateSkillDto addCandidateSKill(CandidateSkillDto candidateSkill);
    public CandidateSkillDto getCandidateSkillById(Integer candidateSkillId);
    public PaginatedResponse<CandidateSkillDto> getAllData(Integer page, Integer size, String sortBy, String sortDir);
    public void deleteCandidateSkill(Integer candidateSKillId);
    public PaginatedResponse<CandidateSkillDto> getCandidateSKillByCandidateId(Integer candidateId,Integer page,Integer size,String sortBy,String sortDir);
    public PaginatedResponse<CandidateSkillDto> getAllCandidatesBySkillId(Integer skillId,Integer page,Integer size,String sortBy,String sortDir);
    public CandidateSkillDto updateCandidateSkill(Integer candidateSkillId, CandidateSkillDto CandidateSkill);

}
