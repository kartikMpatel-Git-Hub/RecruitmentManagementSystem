package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_skill",indexes = {
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,length = 10)
    private Integer skillId;

    @Column(unique = true,length = 30)
    @NotEmpty(message = "Skill Can't Be Empty !")
    @Size(min = 1,max = 30)
    private String skill;
}
