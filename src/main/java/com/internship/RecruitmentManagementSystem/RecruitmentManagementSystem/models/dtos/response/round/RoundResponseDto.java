package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import com.sun.jdi.DoubleValue;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    private String roundFeedback;

    private Double roundRating;

//    private List<InterviewResponseDto> roundInterviews;
}
