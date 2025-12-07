package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewerFeedbackCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview.InterviewerFeedbackUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.InterviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class InterviewController {

    private static final Logger log = LoggerFactory.getLogger(InterviewController.class);
    private final InterviewService interviewService;

    @PostMapping("/")
    public ResponseEntity<String> createInterview(@RequestBody InterviewCreateDto dto) {
        log.info("POST /interviews -> Creating interview with data: {}", dto);
        var createdInterview = interviewService.createInterview(dto);
        log.info("Interview created successfully !");
        return ResponseEntity.status(HttpStatus.CREATED).body("Interviewer Set Successfully !");
    }

    @PutMapping("/{interviewId}")
    public ResponseEntity<InterviewResponseDto> updateInterview(
            @PathVariable Integer interviewId,
            @RequestBody InterviewUpdateDto dto
    ) {
        log.info("PUT /interviews -> Updating interview with data: {}", dto);
        var createdInterview = interviewService.updateInterview(interviewId,dto);
        log.info("Interview Updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(createdInterview);
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponseDto> getInterview(@PathVariable Integer interviewId) {
        log.info("GET /interviews/{} -> Fetching interview with ID: {}", interviewId, interviewId);
        var interview = interviewService.getInterview(interviewId);
        log.info("Fetched interview with ID: {}", interviewId);
        return ResponseEntity.ok(interview);
    }

    @DeleteMapping("/{interviewId}")
    public ResponseEntity<String> deleteInterview(@PathVariable Integer interviewId) {
        log.info("GET /interviews/{} -> Delete interview with ID: {}", interviewId, interviewId);
        interviewService.deleteInterview(interviewId);
        log.info("Deleted interview with ID: {}", interviewId);
        return ResponseEntity.ok("Interview Deleted Successfully");
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<InterviewResponseDto>> getAllInterviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "interviewId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("GET /interviews -> Fetching all interviews (page={}, size={}, sortBy={}, sortDir={})",
                page, size, sortBy, sortDir);

        var response = interviewService.getAllInterviews(page, size, sortBy, sortDir);

        log.info("Fetched {} interviews (Page {}/{})",
                response.getData().size(), response.getCurrentPage() + 1, response.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/complete/{interviewId}")
    public ResponseEntity<?> interviewComplete(
            @PathVariable Integer interviewId
    ){
        log.info("PUT /interviews/complete/{} -> complete interview for interviewId={}",interviewId,interviewId);
        var response = interviewService.interviewComplete(interviewId);
        log.info("Interview Completed With inteviewId={}",interviewId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/round/{roundId}")
    public ResponseEntity<List<InterviewResponseDto>> getInterviewsByRound(@PathVariable Integer roundId) {
        log.info("GET /interviews/round/{} -> Fetching interviews for roundId={}", roundId, roundId);

        var interviews = interviewService.getInterviewsByRound(roundId);

        log.info("Fetched {} interviews for roundId={}", interviews.size(), roundId);
        return ResponseEntity.ok(interviews);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<PaginatedResponse<InterviewResponseDto>> getCandidateInterviews(
            @PathVariable Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "interviewId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("GET /interviews/candidate/{} -> Fetching candidate interviews (page={}, size={})",
                candidateId, page, size);

        var response =
                interviewService.getCandidateInterviews(candidateId, page, size, sortBy, sortDir);

        log.info("Fetched {} candidate interviews for candidateId={} (Page {}/{})",
                response.getData().size(), candidateId, response.getCurrentPage() + 1, response.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interviewer/{interviewerId}")
    public ResponseEntity<PaginatedResponse<InterviewResponseDto>> getInterviewerInterviews(
            @PathVariable Integer interviewerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "interviewId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("GET /interviews/interviewer/{} -> Fetching interviews for interviewerId={} (page={}, size={})",
                interviewerId, interviewerId, page, size);

        var response =
                interviewService.getInterviewerInterviews(interviewerId, page, size, sortBy, sortDir);

        log.info("Fetched {} interviews for interviewerId={} (Page {}/{})",
                response.getData().size(), interviewerId, response.getCurrentPage() + 1, response.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{interviewId}/feedback/{interviewInterviewerId}")
    public ResponseEntity<?> addInterviewerFeedback(
            @PathVariable Integer interviewId,
            @PathVariable Integer interviewInterviewerId,
            @RequestBody InterviewerFeedbackCreateDto interviewerFeedback
    ) {
        log.info("POST /interviews/{}/feedback/{} -> Adding feedback for interviewInterviewerId={}",
                interviewId, interviewInterviewerId, interviewInterviewerId);

        var updatedInterview = interviewService.addFeedbackToInterview(interviewInterviewerId, interviewerFeedback);

        log.info("Added feedback for interviewInterviewerId={} in interviewId={}",
                interviewInterviewerId, interviewId);
        return ResponseEntity.ok(updatedInterview);
    }

    @PutMapping("/{interviewId}/feedbacks/{interviewFeedbackId}")
    public ResponseEntity<?> editInterviewFeedback(
            @PathVariable Integer interviewId,
            @PathVariable Integer interviewFeedbackId,
            @RequestBody InterviewerFeedbackUpdateDto updatingFeedback
    ){
        log.info("PUT /interviews/{}/feedback/{} -> Updating feedback for interviewFeedbackId={}",
                interviewId, interviewFeedbackId, interviewFeedbackId);

        var updatedInterview = interviewService.updateFeedbackById(interviewFeedbackId, updatingFeedback);

        log.info("Updated feedback for interviewFeedbackId={} in interviewId={}",
                interviewFeedbackId, interviewId);
        return ResponseEntity.ok(updatedInterview);
    }

    @GetMapping("/{interviewId}/feedback/{interviewerId}")
    public ResponseEntity<?> getInterviewerFeedback(
            @PathVariable Integer interviewId,
            @PathVariable Integer interviewerId
    ) {
        log.info("GET /interviews/{}/feedback/{} -> Fetching feedback for interviewerId={}",
                interviewId, interviewerId, interviewerId);

        var interviewerFeedback = interviewService.getInterviewerFeedback(interviewId,interviewerId);

        log.info("Fetched feedback for interviewerId={} in interviewId={}",
                interviewerId, interviewId);
        return ResponseEntity.ok(interviewerFeedback);
    }

    @GetMapping("/{interviewId}/feedbacks/{interviewerFeedbackId}")
    public ResponseEntity<?> getInterviewerFeedbackById(
            @PathVariable Integer interviewId,
            @PathVariable Integer interviewerFeedbackId
    ) {
        log.info("GET /interviews/{}/feedback/{} -> Fetching feedback for interviewerFeedbackId={}",
                interviewId, interviewerFeedbackId, interviewerFeedbackId);

        var interviewerFeedback = interviewService.getFeedbackById(interviewerFeedbackId);

        log.info("Fetched feedback for interviewerFeedbackId={} in interviewId={}",
                interviewerFeedbackId, interviewId);
        return ResponseEntity.ok(interviewerFeedback);
    }

    @DeleteMapping("/{interviewId}/feedback/{interviewerFeedbackId}")
    public ResponseEntity<?> removeInterviewerFeedback(
            @PathVariable Integer interviewId,
            @PathVariable Integer interviewerFeedbackId
    ) {
        log.info("DELETE /interviews/{}/feedback/{} -> delete feedback for interviewerFeedbackId={}",
                interviewId, interviewerFeedbackId, interviewerFeedbackId);

        interviewService.deleteFeedback(interviewerFeedbackId);

        log.info("Deleted feedback for interviewerFeedbackId={} in interviewId={}",
                interviewerFeedbackId, interviewId);
        return ResponseEntity.ok("Interviewer Feedback Deleted Successfully");
    }
}
