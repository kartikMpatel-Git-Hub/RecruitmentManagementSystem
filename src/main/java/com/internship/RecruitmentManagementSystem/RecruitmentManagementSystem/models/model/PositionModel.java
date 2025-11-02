package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.PositionType;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_position",indexes = {
        @Index(name = "idx_position_status",columnList = "position_status_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt"),
        @Index(name = "idx_created_by", columnList = "createdBy")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PositionModel extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer positionId;

    @Column(nullable = false,length = 50)
    private String positionTitle;

    @Column(nullable = false,length = 500)
    private String positionDescription;

    @Column(nullable = false,length = 500)
    private String positionCriteria;

    @Column(nullable = false,length = 4)
    private Integer positionTotalOpening;

    @Enumerated(EnumType.STRING)
    private PositionType positionType;

    @Column(nullable = false)
    private Double positionSalary;

    @Column(nullable = false,length = 50)
    private String positionLocation;

    @Column(nullable = false,length = 50)
    private String positionLanguage;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "position_status_id", referencedColumnName = "position_status_id")
    private PositionStatusModel positionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", referencedColumnName = "userId")
    private UserModel createdBy;

    @OneToMany(mappedBy = "position",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<PositionRequirementModel> positionRequirements;

    @OneToMany(mappedBy = "position",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<ApplicationModel> positionApplications;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_position_required_education",
            joinColumns = @JoinColumn(name = "position_id"),
            inverseJoinColumns = @JoinColumn(name = "degree_id")
    )
    private List<DegreeModel> positionRequiredEducations = new ArrayList<>();


}
