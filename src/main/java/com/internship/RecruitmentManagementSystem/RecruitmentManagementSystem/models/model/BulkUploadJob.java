package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_bulk_upload_job")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class BulkUploadJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobId;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private int totalRows;
    private int successRows;
    private int failedRows;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploadedBy", referencedColumnName = "userId")
    private UserModel uploadedBy;
}

