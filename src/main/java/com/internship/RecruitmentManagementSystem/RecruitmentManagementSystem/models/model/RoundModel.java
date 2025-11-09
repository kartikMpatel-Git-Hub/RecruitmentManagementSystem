package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_round",indexes = {
        @Index(name = "idx_application",columnList = "application_id"),
        @Index(name = "idx_round_status",columnList = "round_status_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoundModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicationModel application;

    @Enumerated(EnumType.STRING)
    private RoundType roundType;

    @Enumerated(EnumType.STRING)
    private RoundResult roundResult;

    @FutureOrPresent(message = "Round date must be today or in the future")
    private LocalDate roundDate;

    private LocalTime roundExpectedTime;

    private Integer roundDurationInMinutes;

    private Integer roundSequence;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "round_status_id",referencedColumnName = "round_status_id")
    private RoundStatusModel roundStatus;
}
