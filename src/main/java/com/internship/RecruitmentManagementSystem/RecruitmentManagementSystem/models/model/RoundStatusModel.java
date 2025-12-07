package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
//@Table(name = "tbl_round_status",indexes = {
//        @Index(name = "idx_round_status_id",columnList = "round_status_id"),
//        @Index(name = "idx_created_at", columnList = "createdAt"),
//        @Index(name = "idx_updated_at", columnList = "updatedAt")
//})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class RoundStatusModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_status_id")
    private Integer roundStatusId;

    @Enumerated(EnumType.STRING)
    private RoundStatus roundStatus;

    @Column(length = 300)
    private String roundFeedback;

    private Double rating;

}
