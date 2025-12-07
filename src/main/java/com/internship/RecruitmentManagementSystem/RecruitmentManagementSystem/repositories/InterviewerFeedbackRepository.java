package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.InterviewerFeedbackModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InterviewerFeedbackRepository extends JpaRepository<InterviewerFeedbackModel,Integer> {

    @Query("""
            SELECT t.interviewerFeedback
            FROM InterviewInterviewerModel t
            WHERE t.interview.interviewId = :interviewId
                AND t.interviewer.userId = :interviewerId
        """)
    Optional<InterviewerFeedbackModel> findInterviewerFeedback(@Param("interviewId") Integer interviewId, @Param("interviewerId")Integer interviewerId);
}
