package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.PositionStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionStatusRepository extends JpaRepository<PositionStatusModel,Integer> {
}
