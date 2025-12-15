package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ProficiencyLevel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.MatchingScoreServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchingScoreService implements MatchingScoreServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(MatchingScoreService.class);

    private double proficiencyCalculate(ProficiencyLevel proficiencyLevel) {
        if (proficiencyLevel == null) {
            logger.info("Proficiency level is null → score: 0.0");
            return 0.0;
        }

        double score = switch (proficiencyLevel) {
            case EXPERT -> 4.0;
            case ADVANCED -> 3.0;
            case INTERMEDIATE -> 2.0;
            case BEGINNER -> 1.0;
        };

        logger.info("Calculated proficiency score for {} → {}", proficiencyLevel, score);
        return score;
    }

    @Override
    public double calculateMatchingScore(CandidateModel candidate, PositionModel position) {

        if (candidate == null || position == null) {
            logger.error("Candidate or Position is null while calculating matching score");
            return 0.0;
        }

        logger.info("Starting matching score calculation for CandidateId: {} and PositionId: {}",
                candidate.getCandidateId(), position.getPositionId());

        double skillScore = 0.0, experienceScore = 0.0, educationScore = 0.0;
        double score = 0.0;

        List<PositionRequirementModel> positionRequirements = position.getPositionRequirements();
        if (positionRequirements == null)
            positionRequirements = List.of();

        Map<Integer, CandidateSkillModel> candidateSkillMap = candidate.getCandidateSkills()
                .stream()
                .filter(cs -> cs.getSkill() != null)
                .collect(Collectors.toMap(
                        cs -> cs.getSkill().getSkillId(),
                        cs -> cs
                ));

        logger.debug("Total position skill requirements: {}", positionRequirements.size());
        logger.debug("Candidate skills available: {}", candidateSkillMap.keySet());

        for (PositionRequirementModel requirement : positionRequirements) {

            SkillModel requiredSkill = requirement.getPositionRequiredSkill();
            CandidateSkillModel candidateSkill = candidateSkillMap.get(requiredSkill.getSkillId());

            logger.debug("Evaluating skill requirement '{}' for CandidateId: {}",
                    requiredSkill.getSkill(), candidate.getCandidateId());

            if (candidateSkill == null) {

                logger.warn("CandidateId: {} missing required skill: {}", candidate.getCandidateId(),
                        requiredSkill.getSkill());

//                if (requirement.getPositionRequirement().equals(Requirement.MANDATORY)) {
//                    score -= 15.0;
//                    logger.info("MANDATORY skill missing → -15 points");
//                }
                continue;
            }

            if (requirement.getPositionRequirement().equals(Requirement.MANDATORY)) {
                skillScore += 20.0;
                logger.debug("MANDATORY skill present → +20");
            } else {
                skillScore += 8.0;
                logger.debug("OPTIONAL skill present → +8");
            }

            skillScore += 2 * proficiencyCalculate(candidateSkill.getProficiencyLevel());
            logger.debug("Added proficiency score → new score: {}", skillScore);

            Integer minYear = requirement.getMinYearsOfExperience();
            Integer candYear = candidateSkill.getYearsOfExperience();

            logger.debug("position minimum Experience {} And Candidate Year Of Experience: {}",minYear,candYear);

            if (minYear != null && candYear != null) {
                if (candYear >= minYear) {
                    skillScore += 3.0;
                    logger.debug("Candidate meets minimum experience → +3");

                    if (candYear > minYear) {
                        int bonus = candYear - minYear;
                        skillScore += bonus;
                        logger.debug("Candidate exceeds experience by {} years → +{}", bonus, bonus);
                    }
                }
//                else {
//                    int penalty = minYear - candYear;
//                    score -= penalty;
//                    logger.info("Candidate lacks {} years of experience → -{}", penalty, penalty);
//                }
            }else if (candYear != null) {
                skillScore += Math.min(candYear, 5.0);
                logger.debug("No required experience → candidate experience reward → +{}", Math.min(candYear, 5.0));
            }
        }

        Integer candidateExp = candidate.getCandidateTotalExperienceInYears();
        Integer positionMinExp = position.getPositionMinYearsOfExperience();

        logger.info("Checking total experience match (Candidate: {}, Required: {})",
                candidateExp, positionMinExp);

        if (candidateExp != null && positionMinExp != null) {
            if (candidateExp >= positionMinExp) {
                experienceScore += 20.0;
                logger.debug("Candidate meets min experience → +20");

                if (candidateExp > positionMinExp) {
                    int bonus = candidateExp - positionMinExp;
                    educationScore += bonus;
                    logger.debug("Candidate exceeds exp by {} years → +{}", bonus, bonus);
                }
            }
//            else {
//                int penalty = positionMinExp - candidateExp;
//                score -= penalty;
//                logger.debug("Candidate lacks {} years exp → -{}", penalty, penalty);
//            }
        } else if (candidateExp != null) {
            educationScore += Math.min(candidateExp, 10.0);
            logger.debug("No required exp → candidate exp reward → +{}", Math.min(candidateExp, 10.0));
        }
//        else {
//            score -= 5.0;
//            logger.warn("Candidate has no experience listed → -5 penalty");
//        }
        if (position.getPositionRequiredEducations() != null &&
                !position.getPositionRequiredEducations().isEmpty() &&
                candidate.getCandidateEducations() != null) {

            Set<Integer> requiredDegrees = position.getPositionRequiredEducations()
                    .stream()
                    .map(DegreeModel::getDegreeId)
                    .collect(Collectors.toSet());

            boolean hasRequiredDegree = candidate.getCandidateEducations()
                    .stream()
                    .anyMatch(edu -> edu.getDegree() != null &&
                            requiredDegrees.contains(edu.getDegree().getDegreeId()));

            if (hasRequiredDegree) {
                educationScore += 20.0;
                logger.info("Candidate meets required education → +20");
            }
//            else {
//                score -= 10.0;
//                logger.warn("Candidate does NOT meet required education → -10");
//            }
        } else if (candidate.getCandidateEducations() != null) {
            educationScore += 10.0;
            logger.debug("General education bonus → +10");
        }

        double preNormalized = skillScore + experienceScore + educationScore;
        score = skillScore > 60 ? 60 : skillScore  +
                experienceScore > 20 ? 20 : experienceScore +
                educationScore > 20 ? 20 : educationScore;

        if (score < 0) score = 0.0;
        if (score > 100) score = 100.0;

        logger.info(
                "Final matching score for CandidateId: {} and PositionId: {} → {} (before normalize: {})",
                candidate.getCandidateId(), position.getPositionId(), score, preNormalized
        );

        return score;
    }
}
