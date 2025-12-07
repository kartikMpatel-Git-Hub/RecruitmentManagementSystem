package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_bulk_upload_row_result")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class BulkUploadRowResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer JobRowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobId", nullable = false)
    private BulkUploadJob job;

    private int rowNum;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean success;

    @Column(length = 1000)
    private String errorMessage;
}

