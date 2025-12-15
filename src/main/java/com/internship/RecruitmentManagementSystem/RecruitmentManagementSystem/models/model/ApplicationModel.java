package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_application",indexes = {
        @Index(name = "idx_application_id", columnList = "application_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApplicationModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private PositionModel position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private CandidateModel candidate;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "application_status_id", referencedColumnName = "application_status_id")
    private ApplicationStatusModel applicationStatus;

    private double matchScore;

    private Boolean isShortlisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shortlisted_by")
    private UserModel shortlistedBy;

    private Boolean isSelected;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<RoundModel> rounds = new ArrayList<>();

}
