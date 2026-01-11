package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateModel,Integer> {

    long count();

    @EntityGraph(attributePaths =
            {
                    "user",
                    "candidateSkills",
                    "candidateEducations"}
    )
    Optional<CandidateModel> findById(Integer candidateId);

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
        SELECT DISTINCT c 
        FROM CandidateModel c
        LEFT JOIN FETCH c.candidateSkills cs
        LEFT JOIN FETCH cs.skill
        LEFT JOIN FETCH c.candidateEducations
        ORDER BY c.createdAt DESC
    """)
    Page<CandidateModel> findRecentCandidates(Pageable pageable);

    @Query("SELECT " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears < 1 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears >= 1 AND c.candidateTotalExperienceInYears < 3 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears >= 3 AND c.candidateTotalExperienceInYears < 5 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.candidateTotalExperienceInYears >= 5 THEN 1 ELSE 0 END) " +
            "FROM CandidateModel c")
    List<Object[]> getExperienceDistributionBuckets();

    @Query("""
        SELECT DISTINCT c
        FROM CandidateModel c
        LEFT JOIN FETCH c.candidateSkills cs
        LEFT JOIN FETCH cs.skill
        ORDER BY c.candidateTotalExperienceInYears DESC
    """)
    List<CandidateModel> findTop10ByOrderByCandidateTotalExperienceInYearsDesc();

    @EntityGraph(attributePaths =
            {
                    "user",
                    "candidateSkills",
                    "candidateEducations"
            }
    )
    @Query("""
    select c from CandidateModel c where c.user.userId = :userId
    """)
    Optional<CandidateModel> findByUserId(Integer userId);
    Optional<CandidateModel> findByUserUserEmail(String email);

    boolean existsByUserUserEmail(String email);
    boolean existsByCandidatePhoneNumber(String phone);
    void deleteByUserUserId(Integer userId);

}
