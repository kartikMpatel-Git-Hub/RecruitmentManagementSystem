package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.InterviewInterviewerModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.InterviewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<InterviewModel, Integer> {

    long count();

    @Query("""
        SELECT i.interviewDate, COUNT(i)
        FROM InterviewModel i
        JOIN i.interviewers ii
        WHERE ii.interviewer.userId = :interviewerId
          AND i.interviewDate BETWEEN :startDate AND :endDate
        GROUP BY i.interviewDate
        ORDER BY i.interviewDate
    """)
    List<Object[]> countInterviewsPerDay(
            Integer interviewerId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
        SELECT i.interviewStatus, COUNT(i)
        FROM InterviewModel i
        JOIN i.interviewers ii
        WHERE ii.interviewer.userId = :interviewerId
        GROUP BY i.interviewStatus
    """)
    List<Object[]> countInterviewStatusByInterviewer(Integer interviewerId);

    @Query("""
        SELECT
            SUM(CASE WHEN ii.isFeedbackGiven = true THEN 1 ELSE 0 END),
            SUM(CASE WHEN ii.isFeedbackGiven = false THEN 1 ELSE 0 END)
        FROM InterviewModel i
        JOIN i.interviewers ii
        WHERE ii.interviewer.userId = :interviewerId
          AND i.interviewStatus = 'COMPLETED'
    """)
    Object[] feedbackStatusByInterviewer(Integer interviewerId);


    @Query("""
            select count(i)
            from InterviewModel i
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
            """)
    long countInterviewAssigned(Integer interviewerId);

    @Query("""
            select count(i)
            from InterviewModel i
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
                and (i.interviewDate > :today
                    or (i.interviewDate = :today and i.interviewTime > :now))
    """)
    long upcomingInterviewsByInterviewer(Integer interviewerId,LocalDate today,LocalTime now);

    @Query("""
            select count(i)
            from InterviewModel i
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
                and i.interviewDate = :today
            """)
    long countTodayInterviewByInterviewer(Integer interviewerId,LocalDate today);

    @Query("""
            SELECT COUNT(i)
            FROM InterviewModel i
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
              AND i.interviewStatus = 'COMPLETED'
            """)
    long countCompleteInterviewByInterviewer(Integer interviewerId);

    @Query("""
            select count(i)
            from InterviewModel i
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
                and i.interviewStatus = 'COMPLETED'
                and ii.isFeedbackGiven = false
            """)
    long countPendingFeedbackInterviewByInterviewer(Integer interviewerId);

    @Query("""
            select count(distinct r.application)
            from InterviewModel i
            join RoundModel r on i.round.roundId = r.roundId
            join ApplicationModel a on r.application.applicationId = a.applicationId
            where r.roundType = 'TECHNICAL' or r.roundType = 'HR'
            """)
    long countInterviewedApplications();


    @Query("""
            select count(i)
            from InterviewModel i
            where i.round.application.position.createdBy.userId = :recruiterId
            """)
    long countInterviewsForRecruiter(Integer recruiterId);

    @Query("""
            SELECT COUNT(i) FROM InterviewModel i
            WHERE (i.interviewDate > :todayDate)
               OR (i.interviewDate = :todayDate AND i.interviewTime >= :currentTime)
        """)
    long countUpcomingInterviews(LocalDate todayDate, LocalTime currentTime);

    @Query("""
            select i.interviewId,
                   concat(c.candidateFirstName, ' ', c.candidateLastName) as candidateName,
                   p.positionTitle,
                   i.interviewDate,
                   i.interviewTime,
                   r.roundType,
                   i.interviewStatus,
                   ii.isFeedbackGiven
            from InterviewModel i
            join i.round r
            join r.application a
            join a.candidate c
            join a.position p
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
                and i.interviewDate = :today
            order by i.interviewTime
            """)
    Page<Object[]> findTodayInterviewsByInterviewer(Integer interviewerId,LocalDate today,Pageable page);

    @Query("""
            select i.interviewId,
                   concat(c.candidateFirstName, ' ', c.candidateLastName) as candidateName,
                   p.positionTitle,
                   i.interviewDate,
                   i.interviewTime,
                   r.roundType,
                   i.interviewStatus,
                   false
            from InterviewModel i
            join i.round r
            join r.application a
            join a.candidate c
            join a.position p
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
                and i.interviewDate > :today
                and i.interviewDate <= :futureDate
            order by i.interviewDate, i.interviewTime
            """)
    Page<Object[]> findUpcomingNDaysInterviewsByInterviewer(Integer interviewerId,LocalDate today,LocalDate futureDate,Pageable page);


    @Query("""
            select i.interviewId,
                   concat(c.candidateFirstName, ' ', c.candidateLastName) as candidateName,
                   p.positionTitle,
                   i.interviewDate,
                   i.interviewTime,
                   r.roundType,
                   i.interviewStatus,
                   false
            from InterviewModel i
            join i.round r
            join r.application a
            join a.position p
            join a.candidate c
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
                and i.interviewStatus = 'COMPLETED'
                and ii.isFeedbackGiven = false
            """)
    Page<Object[]> findPendingFeedbackInterviewsByInterviewer(Integer interviewerId,Pageable page);


    @Query("""
            select i.interviewId,
                   concat(c.candidateFirstName, ' ', c.candidateLastName) as candidateName,
                   p.positionTitle,
                   i.interviewDate,
                   i.interviewTime,
                   r.roundType,
                   i.interviewStatus,
                   false
            from InterviewModel i
            join i.round r
            join r.application a
            join a.position p
            join a.candidate c
            join i.interviewers ii
            where ii.interviewer.userId = :interviewerId
                and i.interviewStatus = 'COMPLETED'
            """)
    Page<Object[]> findCompletedInterviewsByInterviewer(Integer interviewerId,Pageable page);

    @Query("""
        SELECT i FROM InterviewModel i
        WHERE (i.interviewDate > :todayDate)
           OR (i.interviewDate = :todayDate AND i.interviewTime >= :currentTime)
        ORDER BY i.interviewDate ASC, i.interviewTime ASC
    """)
    List<InterviewModel> findUpcomingInterviews(LocalDate todayDate, LocalTime currentTime);


    @Query("""
        SELECT i FROM InterviewModel i
        WHERE i.round.application.position.createdBy.userId = :recruiterId
        AND ((i.interviewDate > :todayDate) OR (i.interviewDate = :todayDate AND i.interviewTime >= :currentTime))
        ORDER BY i.interviewDate ASC, i.interviewTime ASC
    """)
    List<InterviewModel> findUpcomingInterviewsByRecruiter(Integer recruiterId,LocalDate todayDate, LocalTime currentTime);

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

    @Query("select i.interviewStatus,count(i) from InterviewModel i group by i.interviewStatus")
    List<Object[]> countInterviewsByStatus();

}

