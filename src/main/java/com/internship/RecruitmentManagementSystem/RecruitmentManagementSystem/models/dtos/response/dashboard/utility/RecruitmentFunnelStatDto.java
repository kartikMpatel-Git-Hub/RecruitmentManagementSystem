package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

@Data
public class RecruitmentFunnelStatDto  {
    private long applications;
    private long shortlisted;
    private long selected;
}
