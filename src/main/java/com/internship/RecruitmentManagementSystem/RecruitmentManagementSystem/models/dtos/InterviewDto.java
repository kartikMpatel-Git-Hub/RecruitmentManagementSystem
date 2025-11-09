package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class InterviewDto {
    private Integer interviewId;
    private Integer roundId;
    private String interviewLink;
    private InterviewStatus interviewStatus;
    private List<Integer> interviewerIds;
}
