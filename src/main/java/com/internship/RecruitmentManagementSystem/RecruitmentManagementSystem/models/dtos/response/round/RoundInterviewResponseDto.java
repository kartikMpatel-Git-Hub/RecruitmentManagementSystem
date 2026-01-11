package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RoundInterviewResponseDto {
    private Integer roundId;

    private Integer applicationId;

    private RoundType roundType;

    private RoundResult roundResult;

    private LocalDate roundDate;

    private LocalTime roundExpectedTime;

    private Integer roundDurationInMinutes;

    private Integer roundSequence;

    private String roundFeedback;

    private Double roundRating;

    private List<InterviewResponseDto> interviews;
}
