package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tbl_interviewer_feedbacks")
@Getter
@Setter
@RequiredArgsConstructor
public class InterviewerFeedbackModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewer_feedback_id")
    private Integer interviewFeedbackId;

    @Column(length = 500)
    private String interviewFeedback;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SkillRatingModel> skillRatings;
}
