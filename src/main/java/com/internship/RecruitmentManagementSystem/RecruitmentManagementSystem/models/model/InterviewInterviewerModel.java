package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_interview_interviewer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewInterviewerModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewInterviewerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interview_id")
    private InterviewModel interview;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interviewer_id")
    private UserModel interviewer;

    private Boolean isFeedbackGiven;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "interviewer_feedback_id")
    private InterviewerFeedbackModel interviewerFeedback;
}
