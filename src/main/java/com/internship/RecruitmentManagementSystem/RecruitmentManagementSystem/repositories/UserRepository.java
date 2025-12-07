package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Integer> {
    Optional<UserModel> findByUserName(String userName);
    Optional<UserModel> findByUserEmail(String userEmail);
    Boolean existsByUserName(String userName);
    Boolean existsByUserEmail(String userEmail);

    @Query("SELECT u FROM UserModel u WHERE LOWER(u.role.role) = 'candidate'")
    Page<UserModel> findCandidate(Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE LOWER(u.role.role) = 'interviewer'")
    Page<UserModel> findInterviewers(Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE LOWER(u.role.role) = 'hr'")
    Page<UserModel> findHrs(Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE LOWER(u.role.role) <> 'candidate'")
    Page<UserModel> findNonCandidate(Pageable pageable);

}
