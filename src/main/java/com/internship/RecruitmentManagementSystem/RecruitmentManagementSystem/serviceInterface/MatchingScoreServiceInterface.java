package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.PositionModel;

public interface MatchingScoreServiceInterface {

    public double calculateMatchingScore(CandidateModel candidate, PositionModel position);

}
