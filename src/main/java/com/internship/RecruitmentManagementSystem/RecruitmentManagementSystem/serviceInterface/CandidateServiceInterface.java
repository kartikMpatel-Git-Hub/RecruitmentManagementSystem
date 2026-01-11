package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.CandidateUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.CandidateRegistrationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CandidateServiceInterface {

    CandidateRegistrationResponse register(UserModel userModel);

    CandidateResponseDto updateCandidate(MultipartFile resume, CandidateUpdateDto newCandidate, Integer candidateId);

    Boolean deleteCandidate(Integer candidateId);

    PaginatedResponse<CandidateResponseDto> getAllCandidates(int page, int size, String sortBy, String sortDir);
//    List<CandidateDto> getAllCandidates();

    CandidateResponseDto getCandidate(Integer candidateId);

    CandidateResponseDto getCandidateByUserId(Integer userId);

    CandidateResponseDto updateCandidateSkills(Integer candidateId, List<Integer> skillIds);

    Long countCandidates();

    CandidateResponseDto processResume(MultipartFile resume);
}
