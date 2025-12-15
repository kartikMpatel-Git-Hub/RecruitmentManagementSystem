package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RegisterModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterRepository extends JpaRepository<RegisterModel,Integer> {

    long count();

    Boolean existsByUserName(String userName);
    Boolean existsByUserEmail(String userEmail);

}
