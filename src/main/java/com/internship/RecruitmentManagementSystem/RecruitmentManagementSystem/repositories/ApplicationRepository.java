package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationModel,Integer> {

    @Query(value = "select count(*) from tbl_application",nativeQuery = true)
    Long countApplications();

    @Query(value = "SELECT position_id FROM tbl_application WHERE candidate_id = :candidateId",nativeQuery = true)
    List<Integer> findAppliedPositionIdsByCandidateId(@Param("candidateId") Integer candidateId);

    Page<ApplicationModel> findByIsShortlistedTrueAndCandidateCandidateId(Integer candidateId,Pageable pageable);
    Page<ApplicationModel> findByIsShortlistedTrueAndPositionPositionId(Integer positionId,Pageable pageable);

    Page<ApplicationModel> findByIsShortlistedTrue(Pageable pageable);

    Page<ApplicationModel> findByCandidateCandidateId(Integer candidateId, Pageable pageable);
    Page<ApplicationModel> findByPositionPositionId(Integer positionId, Pageable pageable);
}
