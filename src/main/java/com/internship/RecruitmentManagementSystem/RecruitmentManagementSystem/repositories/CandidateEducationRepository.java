package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateEducationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateEducationRepository extends JpaRepository<CandidateEducationModel,Integer> {
    Page<CandidateEducationModel> findByCandidate_CandidateId(Integer candidateId, Pageable pageable);
    Page<CandidateEducationModel> findByDegree_DegreeId(Integer degreeId, Pageable pageable);
    Page<CandidateEducationModel> findByUniversity_UniversityId(Integer universityId, Pageable pageable);
}
