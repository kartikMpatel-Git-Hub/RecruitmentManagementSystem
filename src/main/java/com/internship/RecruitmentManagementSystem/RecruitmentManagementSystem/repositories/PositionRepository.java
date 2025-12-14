package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.PositionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PositionRepository extends JpaRepository<PositionModel,Integer> {
    @Query(value = """
            SELECT count(*)
            from tbl_position 
            where position_id 
            in (select position_status_id 
                from tbl_position_status 
                where position_status = 'OPEN')
            """,nativeQuery = true)
    Long countActivePosition();

    @Query("""
            select count(p) from PositionModel p
            where p.positionStatus.positionStatus = 'OPEN'
            and p.createdBy.userId = :recruiterId
            """)
    Long countActivePositionByRecruiter(Integer recruiterId);

    long count();

    @Query("""
            select count(p) from PositionModel p
            where p.createdBy.userId = :recruiterId
            """)
    long countPositionByRecruiter(Integer recruiterId);

    @Query("""
             SELECT p.positionId,
                       p.positionTitle,
                       COUNT(a.applicationId) AS totalApplications,
                       SUM(CASE WHEN a.isShortlisted = true THEN 1 ELSE 0 END) AS shortlistedCount,
                       SUM(CASE WHEN a.applicationStatus.applicationStatus = 'HIRED' THEN 1 ELSE 0 END) AS hiredCount
                FROM PositionModel p
                JOIN ApplicationModel a on p.positionId = a.position.positionId
                GROUP BY p.positionId, p.positionTitle
            """)
    Page<Object[]> findPositionAnalysis(Pageable pageable);

    @Query("""
            select
                p.positionId,
                p.positionTitle,
                p.positionStatus.positionStatus,
                count(a),
                sum(case when a.isShortlisted = true then 1 else 0 end),
                sum(case when a.applicationStatus.applicationStatus = 'HIRED' then 1 else 0 end),
                sum(case when a.applicationStatus.applicationStatus = 'REJECTED' then 1 else 0 end),
                p.createdAt
            from PositionModel p
            left join ApplicationModel a on p.positionId = a.position.positionId
            where p.createdBy.userId = :recruiterId
            group by p.positionId, p.positionTitle, p.positionStatus.positionStatus, p.createdAt
    """)
    Page<Object[]> getPositionsOverviewByRecruiter(Integer recruiterId,Pageable pageable);

    @Query("""
            select p.positionId,
                   p.positionTitle,
                   count(a) as totalApplications,
                   SUM(case when a.applicationStatus.applicationStatus = 'SHORTLISTED' then 1 else 0 end) * 100.0 / count(a) as averageShortlistedScore,
                   SUM(case when a.applicationStatus.applicationStatus = 'MAPPED' then 1 else 0 end) * 100.0 / count(a) as averageMappedScore,
                   SUM(case when a.applicationStatus.applicationStatus = 'HIRED' then 1 else 0 end) * 100.0 / count(a) as averageHiredScore,
                   SUM(case when a.applicationStatus.applicationStatus = 'REJECTED' then 1 else 0 end) * 100.0 / count(a) as averageRejectScore
            from PositionModel p
            left join p.positionApplications a
            where p.createdBy.userId = :recruiterId
            group by p.positionId, p.positionTitle
            """)
    List<Object[]> getPositionPerformanceMetrics(Integer recruiterId);

    @Query("""
            select p
            from PositionModel p
            where p.createdBy.userId = :recruiterId
            """)
    Page<PositionModel> findPositionByRecruiter(Integer recruiterId,Pageable pageable);

}
