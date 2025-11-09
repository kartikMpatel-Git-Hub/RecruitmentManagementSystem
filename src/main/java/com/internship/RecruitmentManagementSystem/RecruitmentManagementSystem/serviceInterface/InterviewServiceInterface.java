package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.InterviewCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.InterviewUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.InterviewerFeedbackCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.InterviewerFeedbackResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface InterviewServiceInterface {
    InterviewResponseDto createInterview(InterviewCreateDto dto);
    InterviewResponseDto getInterview(Integer interviewId);
    void deleteInterview(Integer interviewId);
    InterviewResponseDto updateInterview(Integer interviewId, InterviewUpdateDto dto);
    List<InterviewResponseDto> getInterviewsByRound(Integer roundId);
    PaginatedResponse<InterviewResponseDto> getAllInterviews(Integer page, Integer size, String sortBy, String sortDir);
    PaginatedResponse<InterviewResponseDto> getCandidateInterviews(Integer candidateId,Integer page, Integer size, String sortBy, String sortDir);
    PaginatedResponse<InterviewResponseDto> getInterviewerInterviews(Integer interviewerId,Integer page, Integer size, String sortBy, String sortDir);
    InterviewerFeedbackResponseDto addFeedbackToInterview(Integer interviewInterviewerId, InterviewerFeedbackCreateDto feedbackCreateDto);
    InterviewerFeedbackResponseDto getFeedbackById(Integer feedbackId);
    void deleteFeedback(Integer feedbackId);
}
