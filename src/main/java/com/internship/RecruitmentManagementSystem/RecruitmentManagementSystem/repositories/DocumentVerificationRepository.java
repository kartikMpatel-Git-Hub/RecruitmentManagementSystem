package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentVerificationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DocumentVerificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentVerificationRepository extends JpaRepository<DocumentVerificationModel,Integer> {

    Optional<DocumentVerificationModel> findByApplicationApplicationId(Integer applicationId);

    Page<DocumentVerificationModel> findByVerificationStatus(
            DocumentVerificationStatus status,
            Pageable pageable
    );
}
