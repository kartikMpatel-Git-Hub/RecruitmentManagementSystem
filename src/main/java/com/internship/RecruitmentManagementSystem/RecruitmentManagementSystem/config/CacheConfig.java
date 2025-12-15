package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "degreeData",
                "currentUserData",
                "currentCandidateData",
                "candidateData",
                "skillData",
                "universityData",
                "userData",
                "userNonCandidateData",
                "paginatedData",
                "userCandidateData",
                "userDetails",
                "userCandidate",
                "userSkill",
                "userDegree",
                "userUniversity",
                "candidateEducationData",
                "candidateEducation",
                "candidateSkillData",
                "candidateSkill",
                "positionData",
                "applicationData",
                "shortlistedApplicationData",
                "roundData",
                "interviewData",
                "bulkUploadJobData",
                "registerRequest",
                "documentVerification"
        );
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
