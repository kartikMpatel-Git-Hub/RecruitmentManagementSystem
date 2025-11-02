package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.DegreeDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "position_id")
    private PositionModel position;

    @Enumerated(EnumType.STRING)
    private Requirement positionRequirement;


}
