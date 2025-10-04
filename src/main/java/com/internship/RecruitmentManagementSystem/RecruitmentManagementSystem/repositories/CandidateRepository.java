package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<CandidateModel,Integer> {
    Optional<CandidateModel> findByUserUserId(Integer userId);
    Optional<CandidateModel> findByUserUserEmail(String email);

    boolean existsByUserUserEmail(String email);
    boolean existsByCandidatePhoneNumber(String phone);
}
