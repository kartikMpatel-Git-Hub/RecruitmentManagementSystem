package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "tbl_application_status",indexes = {
        @Index(name = "idx_application_status", columnList = "application_status"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApplicationStatusModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_status_id")
    private Integer applicationStatusId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ApplicationStatus applicationStatus;

    @Column(columnDefinition = "TEXT")
    private String applicationFeedback;

}
