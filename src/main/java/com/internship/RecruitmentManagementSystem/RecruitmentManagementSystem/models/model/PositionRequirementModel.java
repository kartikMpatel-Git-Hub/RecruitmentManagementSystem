package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "tbl_position_requirement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PositionRequirementModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer positionRequirementId;

    @ManyToOne
    @JoinColumn(name = "position_required_skill_id")
    private SkillModel positionRequiredSkill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private PositionModel position;

    private Integer minYearsOfExperience;

    @Enumerated(EnumType.STRING)
    private Requirement positionRequirement;


}
