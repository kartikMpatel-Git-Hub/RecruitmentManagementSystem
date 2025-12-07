package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.InterviewInterviewerModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface InterviewInterviewerRepository extends JpaRepository<InterviewInterviewerModel, Integer> {
    List<InterviewInterviewerModel> findByInterviewInterviewId(Integer interviewId);

    @Query("""
       SELECT ii.interviewer.userId AS interviewerId, COUNT(ii) AS load
       FROM InterviewInterviewerModel ii
       GROUP BY ii.interviewer.userId
       """)
    List<Object[]> getInterviewerLoad();

    @Query("""
       SELECT u FROM UserModel u 
       WHERE u.role.role = 'INTERVIEWER'
       AND u.userId NOT IN (
            SELECT ii.interviewer.userId 
            FROM InterviewInterviewerModel ii
            WHERE ii.interview.interviewDate = :date
            AND (
                 ii.interview.interviewTime < :endTime
                 AND ii.interview.interviewEndTime > :startTime
            )
       )
       """)
    List<UserModel> findFreeInterviewers(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("""
       SELECT u FROM UserModel u 
       WHERE u.role.role = 'HR'
       AND u.userId NOT IN (
            SELECT ii.interviewer.userId 
            FROM InterviewInterviewerModel ii
            WHERE ii.interview.interviewDate = :date
            AND (
                 ii.interview.interviewTime < :endTime
                 AND ii.interview.interviewEndTime > :startTime
            )
       )
       """)
    List<UserModel> findFreeHrs(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );


}