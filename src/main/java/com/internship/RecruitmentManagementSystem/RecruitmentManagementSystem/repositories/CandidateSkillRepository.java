package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateEducationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateSkillModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkillModel,Integer> {

    Page<CandidateSkillModel> findByCandidate_CandidateId(Integer candidateId, Pageable pageable);
    Page<CandidateSkillModel> findBySkill_SkillId(Integer skillId, Pageable pageable);

}
