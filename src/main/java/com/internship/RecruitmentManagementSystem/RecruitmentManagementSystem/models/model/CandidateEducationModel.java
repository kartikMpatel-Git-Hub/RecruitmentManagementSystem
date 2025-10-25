package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "tbl_candidate_education",indexes = {
        @Index(name = "idx_candidate_id", columnList = "candidate_id"),
        @Index(name = "idx_university_id", columnList = "university_id"),
        @Index(name = "idx_degree_id", columnList = "degree_id"),
        @Index(name = "idx_passing_year", columnList = "passingYear"),
        @Index(name = "idx_candidate_degree", columnList = "candidate_id, degree_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateEducationModel extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer candidateEducationId;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    CandidateModel candidate;

    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    UniversityModel university;

    @ManyToOne
    @JoinColumn(name = "degree_id", nullable = false)
    DegreeModel degree;

    Double percentage;

    Integer passingYear;
}
