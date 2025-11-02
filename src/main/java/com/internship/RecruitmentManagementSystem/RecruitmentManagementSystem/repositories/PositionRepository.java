package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.PositionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionRepository extends JpaRepository<PositionModel,Integer> {
    @Query(value = "SELECT count(*) from tbl_position where position_id in (select position_status_id from tbl_position_status where position_status = 'OPEN')",nativeQuery = true)
    Long countActivePosition();
}
