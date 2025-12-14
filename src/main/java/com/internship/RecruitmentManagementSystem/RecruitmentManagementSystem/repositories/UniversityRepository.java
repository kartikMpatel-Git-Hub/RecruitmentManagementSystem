package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UniversityModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<UniversityModel,Integer> {
    boolean existsByUniversity(String university);
    long count();

    @Query("select u.university ,count(u) from UniversityModel u group by u.university order by count(u) desc")
    Page<Object[]> findTopUniversities(Pageable pageable);

    Optional<UniversityModel> findByUniversity(String universityName);
//    Page<UniversityModel> findAll(Pageable pageable);
}
