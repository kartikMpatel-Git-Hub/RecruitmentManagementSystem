package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.InterviewInterviewerModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewInterviewerRepository extends JpaRepository<InterviewInterviewerModel, Integer> {
    List<InterviewInterviewerModel> findByInterviewInterviewId(Integer interviewId);
}