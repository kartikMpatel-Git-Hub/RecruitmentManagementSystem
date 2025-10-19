package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateEducationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateEducationRepository extends JpaRepository<CandidateEducationModel,Integer> {
    Optional<CandidateEducationModel> findByCandidate_CandidateId(Integer candidateId);
    Optional<CandidateEducationModel> findByCandidate_Degree_DegreeId(Integer degreeId);
    Optional<CandidateEducationModel> findByCandidate_University_UniversityId(Integer universityId);
}
