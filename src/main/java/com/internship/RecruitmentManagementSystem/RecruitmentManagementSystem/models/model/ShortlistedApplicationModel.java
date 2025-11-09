package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.List;

//@Entity
//@Table(name = "tbl_shortlisted_application",indexes = {
//        @Index(name = "idx_shortlisted_application_id", columnList = "shortlisted_application_id"),
//        @Index(name = "idx_application_id", columnList = "application_id"),
//        @Index(name = "idx_created_at", columnList = "createdAt"),
//        @Index(name = "idx_updated_at", columnList = "updatedAt")
//})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ShortlistedApplicationModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shortlisted_application_id")
    private Integer shortlistedApplicationId;

    @OneToOne
    @JoinColumn(name = "application_id", referencedColumnName = "application_id")
    private ApplicationModel application;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "shortlisted_application_status_id", referencedColumnName = "shortlisted_application_status_id")
    private ShortlistedApplicationStatusModel shortlistedApplicationStatus;

    @OneToMany(mappedBy = "shortlistedApplication",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<RoundModel> rounds;

}
