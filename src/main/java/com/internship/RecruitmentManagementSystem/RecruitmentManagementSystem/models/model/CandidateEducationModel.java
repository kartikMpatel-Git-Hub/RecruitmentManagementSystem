package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_candidate_education")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateEducationModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer candidateEducationId;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    CandidateModel candidate;

    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    UniversityModel university;

    @ManyToOne
    @JoinColumn(name = "degree_id", nullable = false)
    DegreeModel degree;

}
