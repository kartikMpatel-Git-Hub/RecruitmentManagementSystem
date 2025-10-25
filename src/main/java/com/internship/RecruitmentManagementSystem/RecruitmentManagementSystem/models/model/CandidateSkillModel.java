package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ProficiencyLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_candidate_skill",indexes = {
        @Index(name = "idx_candidate_id", columnList = "candidate_id"),
        @Index(name = "idx_skill_id", columnList = "skill_id"),
        @Index(name = "idx_proficiency_level", columnList = "proficiency_level"),
        @Index(name = "idx_year_of_experience", columnList = "yearsOfExperience"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateSkillModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer candidateSkillId;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private CandidateModel candidate;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private SkillModel skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProficiencyLevel proficiencyLevel;

    private Integer yearsOfExperience;
}
