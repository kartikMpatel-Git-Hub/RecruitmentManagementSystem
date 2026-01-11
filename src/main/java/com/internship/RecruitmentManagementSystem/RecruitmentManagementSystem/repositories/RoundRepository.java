package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoundModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoundRepository extends JpaRepository<RoundModel,Integer> {

    List<RoundModel> findByApplicationApplicationId(Integer applicationId);
    Page<RoundModel> findByApplicationApplicationId(Integer applicationId,Pageable pageable);

    Page<RoundModel> findByApplicationCandidateCandidateId(Integer candidateId,Pageable pageable);

    Page<RoundModel> findByApplicationPositionPositionId(Integer positionId,Pageable pageable);

}
