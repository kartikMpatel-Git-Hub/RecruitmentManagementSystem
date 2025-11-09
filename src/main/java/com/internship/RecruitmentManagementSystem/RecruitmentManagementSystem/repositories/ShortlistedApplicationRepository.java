package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ShortlistedApplicationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ShortlistedApplicationRepository
//        extends JpaRepository<ShortlistedApplicationModel,Integer>
{

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"application.candidate", "application.position"})
    Page<ShortlistedApplicationModel> findByApplicationCandidateCandidateId(Integer candidateId,Pageable pageable);

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"application.candidate", "application.position"})
    Page<ShortlistedApplicationModel> findByApplicationPositionPositionId(Integer positionId,Pageable pageable);

}
