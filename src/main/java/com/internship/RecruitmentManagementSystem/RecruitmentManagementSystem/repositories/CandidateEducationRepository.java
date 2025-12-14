package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateEducationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateEducationRepository extends JpaRepository<CandidateEducationModel,Integer> {
    Page<CandidateEducationModel> findByCandidate_CandidateId(Integer candidateId, Pageable pageable);
    Page<CandidateEducationModel> findByDegree_DegreeId(Integer degreeId, Pageable pageable);
    Page<CandidateEducationModel> findByUniversity_UniversityId(Integer universityId, Pageable pageable);

    @Query("select ce.degree.degree ,count(ce) from CandidateEducationModel ce group by ce.degree.degree order by count(ce) desc")
    Page<Object[]> findTopDegrees(Pageable pageable);

    @Query("select ce.university.university ,count(ce) from CandidateEducationModel ce group by ce.university.university order by count(ce) desc")
    Page<Object[]> findTopUniversities(Pageable pageable);


}
