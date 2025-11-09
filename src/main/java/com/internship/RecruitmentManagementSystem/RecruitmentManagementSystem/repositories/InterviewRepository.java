package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.InterviewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterviewRepository extends JpaRepository<InterviewModel, Integer> {

    List<InterviewModel> findByRoundRoundId(Integer roundId);

    @Query("SELECT i FROM InterviewModel i " +
            "JOIN i.round r " +
            "JOIN r.application a " +
            "WHERE a.candidate.candidateId = :candidateId")
    Page<InterviewModel> findInterviewsByCandidateId(
            @Param("candidateId") Integer candidateId,
            Pageable pageable);

    @Query("""
       SELECT i FROM InterviewModel i
       JOIN i.interviewers ii
       WHERE ii.interviewer.userId = :interviewerId
       """)
    Page<InterviewModel> findByInterviewerId(@Param("interviewerId") Integer interviewerId,Pageable pageable);

}

