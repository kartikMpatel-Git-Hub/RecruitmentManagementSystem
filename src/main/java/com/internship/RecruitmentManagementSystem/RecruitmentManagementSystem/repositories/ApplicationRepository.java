package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationModel,Integer> {

    long count();

    @Query("""
                    select count(a)
                    from ApplicationModel a
                    where a.applicationStatus.applicationStatus in ("UNDERPROCESS","MAPPED")
            """)
    long countPendingApplications();

    @Query("""
            select count(*)
            from ApplicationModel a
            where a.isShortlisted is true
            and a.shortlistedBy.userId = :reviewerId
            AND DATE(a.updatedAt) = CURRENT_DATE
            """)
    long countReviewedTodayByReviewer(Integer reviewerId);

    @Query("""
            select count(a)
            from ApplicationModel a
            where a.position.createdBy.userId = :recruiterId
            """)
    long countApplicationsByRecruiter(Integer recruiterId);

    @Query("select a.applicationStatus.applicationStatus, count(a) " +
            "from ApplicationModel a " +
            "group by a.applicationStatus.applicationStatus")
    List<Object[]> countApplicationsByStatus();

    @Query("""
            select a.applicationStatus.applicationStatus, count(a)
            from ApplicationModel a
            where a.position.createdBy.userId = :recruiterId
            group by a.applicationStatus.applicationStatus
            """)
    List<Object[]> countApplicationsStatusByRecruiter(Integer recruiterId);

    @Query("select count(a) from ApplicationModel a where a.applicationStatus.applicationStatus = :applicationStatus")
    long countByApplicationStatus(ApplicationStatus applicationStatus);


    @Query("""
            select count(a)
            from ApplicationModel a
            join PositionModel p on a.position.positionId = p.positionId
            where p.positionId = :positionId
            and a.applicationStatus.applicationStatus = :applicationStatus
            """)
    long countByStatusForRecruiter(Integer positionId, ApplicationStatus applicationStatus);

    @Query("SELECT function('date', a.createdAt), COUNT(a) " +
            "FROM ApplicationModel a " +
            "WHERE a.createdAt BETWEEN :start AND :end " +
            "GROUP BY function('date', a.createdAt) " +
            "ORDER BY function('date', a.createdAt)")
    List<Object[]> countApplicationsByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT function('date', a.createdAt), COUNT(a) " +
            "FROM ApplicationModel a " +
            "WHERE a.position.createdBy.userId = :recruiterId" +
            " AND a.createdAt BETWEEN :start AND :end " +
            "GROUP BY function('date', a.createdAt) " +
            "ORDER BY function('date', a.createdAt)")
    List<Object[]> countApplicationsByDateRangeAndRecruiter(Integer recruiterId, LocalDateTime start, LocalDateTime end);

    @Query("""
            select a.applicationId,
                   CONCAT(c.candidateFirstName, ' ', c.candidateMiddleName, ' ', c.candidateLastName) as candidateName,
                   p.positionTitle,
                   DATE(a.createdAt) as applicationDate,
                   a.applicationStatus.applicationStatus
            from ApplicationModel a
            join a.candidate c
            join a.position p
            where p.createdBy.userId = :recruiterId
            order by a.createdAt desc
            """)
    List<Object[]> findRecentApplicationsByRecruiter(Integer recruiterId, Pageable pageable);


    @Query("""
            select a.applicationId,
                   CONCAT(c.candidateFirstName, ' ', c.candidateMiddleName, ' ', c.candidateLastName) as candidateName,
                    c.candidateTotalExperienceInYears,
                    p.positionTitle,
                   DATE(a.createdAt) as applicationDate,
                   a.applicationStatus.applicationStatus as applicationStatus
            from ApplicationModel a
            join a.candidate c
            join a.position p
            order by a.createdAt desc
            """)
    List<Object[]> findRecentApplications(Pageable pageable);

    @Query("""
            select a.applicationId,
                   CONCAT(c.candidateFirstName, ' ', c.candidateMiddleName, ' ', c.candidateLastName) as candidateName,
                    c.candidateTotalExperienceInYears,
                    p.positionTitle,
                   DATE(a.createdAt) as applicationDate,
                   a.applicationStatus.applicationStatus as applicationStatus
            from ApplicationModel a
            join a.candidate c
            join a.position p
            where a.applicationStatus.applicationStatus in ("UNDERPROCESS","MAPPED")
            order by a.createdAt ASC
            """)
    List<Object[]> findPendingReviewApplications();

    @Query("""
            select a.applicationId,
                   CONCAT(c.candidateFirstName, ' ', c.candidateMiddleName, ' ', c.candidateLastName) as candidateName,
                   c.candidateTotalExperienceInYears,
                   p.positionTitle,
                   DATE(a.createdAt) as applicationDate,
                   a.applicationStatus.applicationStatus as applicationStatus
            from ApplicationModel a
            join a.candidate c
            join a.position p
            where a.isShortlisted is true
            and a.shortlistedBy.userId = :reviewerId
            order by a.createdAt DESC
            """)
    Page<Object[]> findApplicationShortlistedByReviewer(Integer reviewerId, Pageable pageable);

    @Query("""
            select count(*)
            from ApplicationModel a
            where a.isShortlisted is true
            and a.shortlistedBy.userId = :reviewerId
            """)
    long countShortlistedByReviewer(Integer reviewerId);

    @Query("SELECT function('month', a.createdAt), COUNT(a) " +
            "FROM ApplicationModel a " +
            "WHERE a.applicationStatus.applicationStatus = :status " +
            "AND a.createdAt BETWEEN :start AND :end " +
            "GROUP BY function('month', a.createdAt) " +
            "ORDER BY function('month', a.createdAt)")
    List<Object[]> countStatusApplicationsByMonth(LocalDateTime start, LocalDateTime end, ApplicationStatus status);

    @Query("select a.position.positionTitle,count(a) from ApplicationModel a group by a.position.positionTitle")
    List<Object[]> countApplicationsByPosition();

    Boolean existsByCandidateCandidateIdAndPositionPositionId(Integer candidateId, Integer positionId);

    @Query(value = "SELECT position_id FROM tbl_application WHERE candidate_id = :candidateId", nativeQuery = true)
    List<Integer> findAppliedPositionIdsByCandidateId(@Param("candidateId") Integer candidateId);

    Page<ApplicationModel> findByApplicationStatusApplicationStatusAndPositionPositionId(ApplicationStatus applicationStatus, Integer positionId, Pageable pageable);

    Page<ApplicationModel> findByIsShortlistedTrueAndCandidateCandidateId(Integer candidateId, Pageable pageable);

    Page<ApplicationModel> findByIsShortlistedTrueAndPositionPositionId(Integer positionId, Pageable pageable);

    Page<ApplicationModel> findByIsShortlistedTrue(Pageable pageable);

    long countByIsShortlistedTrue();

    Page<ApplicationModel> findByCandidateCandidateId(Integer candidateId, Pageable pageable);

    Page<ApplicationModel> findByPositionPositionId(Integer positionId, Pageable pageable);

    @Query("""
            select a.applicationId,
                   CONCAT(c.candidateFirstName, ' ', c.candidateMiddleName, ' ', c.candidateLastName) as candidateName,
                    c.candidateTotalExperienceInYears,
                    p.positionTitle,
                   DATE(a.createdAt) as applicationDate,
                   a.applicationStatus.applicationStatus as applicationStatus
            from ApplicationModel a
            join a.candidate c
            join a.position p
            order by a.createdAt ASC
            """)
    Page<Object[]> findAllApplications(Pageable page);

    @Query("""
            select a
            from ApplicationModel a
            join a.position p
            where p.createdBy.userId = :recruiterId
            and a.isShortlisted is true
            """)
    Page<ApplicationModel> findRecruiterShortlists(Integer recruiterId, Pageable page);

    @Query("""
            select a
            from ApplicationModel a
            where a.shortlistedBy.userId = :recruiterId
            and a.isShortlisted is true
            """)
    Page<ApplicationModel> findShortlistsByReviewer(Integer recruiterId, Pageable page);

    @Query("""
            select a
            from ApplicationModel a
            join a.position p
            where p.createdBy.userId = :recruiterId
            """)
    Page<ApplicationModel> findRecruiterApplications(Integer recruiterId, Pageable page);

    @Query("""
            select a
            from ApplicationModel a
            where a.position.positionId = :positionId
            and a.isShortlisted is true
            and a.shortlistedBy.userId = :reviewerId
            """)
    Page<ApplicationModel> findByShortlistedByPositionAndReviewer(Integer positionId,Integer reviewerId,Pageable pageable);
}
