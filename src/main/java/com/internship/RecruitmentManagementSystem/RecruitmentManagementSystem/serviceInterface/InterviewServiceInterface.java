package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewerFeedbackCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewerFeedbackUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewerFeedbackResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface InterviewServiceInterface {
    Boolean createInterview(InterviewCreateDto dto);
    InterviewResponseDto getInterview(Integer interviewId);
    InterviewResponseDto interviewComplete(Integer interviewId);
    void deleteInterview(Integer interviewId);
    InterviewResponseDto updateInterview(Integer interviewId, InterviewUpdateDto dto);
    List<InterviewResponseDto> getInterviewsByRound(Integer roundId);
    PaginatedResponse<InterviewResponseDto> getAllInterviews(Integer page, Integer size, String sortBy, String sortDir);
    PaginatedResponse<InterviewResponseDto> getCandidateInterviews(Integer candidateId,Integer page, Integer size, String sortBy, String sortDir);
    PaginatedResponse<InterviewResponseDto> getInterviewerInterviews(Integer interviewerId,Integer page, Integer size, String sortBy, String sortDir);
    InterviewerFeedbackResponseDto addFeedbackToInterview(Integer interviewInterviewerId, InterviewerFeedbackCreateDto feedbackCreateDto);
    InterviewerFeedbackResponseDto getFeedbackById(Integer feedbackId);
    InterviewerFeedbackResponseDto updateFeedbackById(Integer feedbackId,InterviewerFeedbackUpdateDto updatingFeedback);
    InterviewerFeedbackResponseDto getInterviewerFeedback(Integer interviewId,Integer interviewerId);
    void deleteFeedback(Integer feedbackId);
}
//