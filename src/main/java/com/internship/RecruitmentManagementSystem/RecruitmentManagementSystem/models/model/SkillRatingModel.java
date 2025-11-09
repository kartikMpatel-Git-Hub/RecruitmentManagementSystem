package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "tbl_skill_rating")
@Data
@RequiredArgsConstructor
public class SkillRatingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer skillRatingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id")
    private InterviewerFeedbackModel feedback;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillModel skill;

    private Double skillRating;

    @Column(length = 200)
    private String skillFeedback;

}
