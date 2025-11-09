package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ShortlistedApplicationStaus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

//@Entity
//@Table(name = "tbl_shortlisted_application_status",indexes = {
//        @Index(name = "idx_shortlisted_application_status", columnList = "shortlisted_application_status"),
//        @Index(name = "idx_created_at", columnList = "createdAt"),
//        @Index(name = "idx_updated_at", columnList = "updatedAt")
//})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ShortlistedApplicationStatusModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shortlisted_application_status_id")
    private Integer shortlistedApplicationStatusId;

    @Enumerated(EnumType.STRING)
    private ShortlistedApplicationStaus shortlistedApplicationStatus;

    @Column(length = 300)
    private String shortlistedApplicationFeedback;

}
