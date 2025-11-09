package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class RoundResponseDto {

    private Integer roundId;

    private RoundType roundType;

    private RoundResult roundResult;

    private LocalDate roundDate;

    private LocalTime roundExpectedTime;

    private Integer roundDurationInMinutes;

    private Integer roundSequence;

    private RoundStatusResponseDto roundStatus;
}
