package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadRowResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkUploadRowResultRepository extends JpaRepository<BulkUploadRowResult,Integer> {
    List<BulkUploadRowResult> findByJob(BulkUploadJob job);
}
