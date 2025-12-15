package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentVerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "tbl_document_verification")
@Getter
@Setter
public class DocumentVerificationModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer documentVerificationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private ApplicationModel application;

    @Enumerated(EnumType.STRING)
    private DocumentVerificationStatus verificationStatus;

    private String hrRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private UserModel verifiedBy;

    private LocalDateTime verifiedAt;

    @OneToMany(
            mappedBy = "documentVerification",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DocumentModel> documents = new ArrayList<>();
}
