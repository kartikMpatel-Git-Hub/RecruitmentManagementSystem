package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.CandidateRegistrationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface CandidateServiceInterface {

    public CandidateRegistrationResponse register(UserModel userModel);

    public CandidateDto updateCandidate(CandidateDto newCandidate, Integer candidateId);

    public Boolean deleteCandidate(Integer candidateId);

    public PaginatedResponse<CandidateDto> getAllCandidates(int page, int size, String sortBy, String sortDir);
//    List<CandidateDto> getAllCandidates();

    CandidateDto getCandidate(Integer candidateId);

    CandidateDto getCandidateByUserId(Integer userId);

    CandidateDto updateCandidateSkills(Integer candidateId, List<Integer> skillIds);

    Long countCandidates();
}
