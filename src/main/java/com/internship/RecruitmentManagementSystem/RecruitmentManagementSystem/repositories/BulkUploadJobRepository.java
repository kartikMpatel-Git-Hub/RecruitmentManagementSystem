package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkUploadJobRepository extends JpaRepository<BulkUploadJob, Integer> {
}
