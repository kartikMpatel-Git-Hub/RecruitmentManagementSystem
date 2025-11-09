package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_position_round",indexes = {
        @Index(name = "idx_position",columnList = "positionId"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionRoundModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer positionRoundId;

    @ManyToOne
    @JoinColumn(name = "positionId",nullable = false)
    private PositionModel position;

    private LocalDate positionRoundExpectedDate;

    private LocalTime positionRoundExpectedTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoundType positionRoundType;

    private Integer positionRoundSequence;
}
