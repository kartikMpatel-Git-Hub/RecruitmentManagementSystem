package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateModel,Integer> {

    long count();

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT c FROM CandidateModel c ORDER BY c.createdAt DESC")
    Page<CandidateModel> findRecentCandidates(Pageable pageable);

    @Query("SELECT " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears < 1 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears >= 1 AND c.candidateTotalExperienceInYears < 3 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears >= 3 AND c.candidateTotalExperienceInYears < 5 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears >= 5 THEN 1 ELSE 0 END) " +
            "FROM CandidateModel c")
    List<Object[]> getExperienceDistributionBuckets();

    List<CandidateModel> findTop10ByOrderByCandidateTotalExperienceInYearsDesc();

    Optional<CandidateModel> findByUserUserId(Integer userId);
    Optional<CandidateModel> findByUserUserEmail(String email);

    boolean existsByUserUserEmail(String email);
    boolean existsByCandidatePhoneNumber(String phone);
    void deleteByUserUserId(Integer userId);

}
