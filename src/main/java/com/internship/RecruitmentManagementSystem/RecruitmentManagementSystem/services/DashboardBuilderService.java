package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.admin.AdminSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr.HrSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.PositionPerformanceDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.RecruiterPositionOverviewDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.RecruiterRecentApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.RecruiterSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.InterviewModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.DashboardBuilderServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardBuilderService implements DashboardBuilderServiceInterface {

    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final PositionRepository positionRepository;
    private final SkillRepository skillRepository;
    private final CandidateEducationRepository candidateEducationRepository;
    private final UniversityRepository universityRepository;
    private final DegreeRepository degreeRepository;
    private final RegisterRepository registerRepository;

    @Override 
    public  HrSummaryStatsDto buildHrSummaryStats() {
        long totalCandidates = candidateRepository.count();
        long totalApplications = applicationRepository.count();
        long totalOpenPositions = positionRepository.countActivePosition();
        long upcomingInterviewsCount = interviewRepository.countUpcomingInterviews(LocalDate.now(), LocalTime.now());
        long totalDegrees = degreeRepository.count();
        long totalSkills = skillRepository.count();
        long totalUniversities = universityRepository.count();
        long newCandidatesToday = candidateRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );
        HrSummaryStatsDto dto = new HrSummaryStatsDto();
        dto.setTotalCandidates(totalCandidates);
        dto.setNewCandidatesToday(newCandidatesToday);
        dto.setTotalApplications(totalApplications);
        dto.setTotalOpenPositions(totalOpenPositions);
        dto.setTotalDegrees(totalDegrees);
        dto.setTotalSkills(totalSkills);
        dto.setTotalUniversities(totalUniversities);
        dto.setUpcomingInterviewsCount(upcomingInterviewsCount);
        return dto;
    }

    @Override 
    public List<UniversityCountDto> buildTopUniversities() {
        Pageable page = PageRequest.of(0,10);
        Page<Object[]> result = candidateEducationRepository.findTopUniversities(page);
        List<UniversityCountDto> response = new ArrayList<>();
        for(Object[] row : result){
            UniversityCountDto dto = new UniversityCountDto();
            dto.setUniversity(row[0] != null ? row[0].toString() : "UNKNOWN");
            dto.setCount(row[1] != null ? ((Number)row[1]).longValue() : 0L);
            response.add(dto);
        }
        return response;
    }

    @Override 
    public List<MonthlyHiringStatDto> buildMonthlyHiringStats() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusMonths(12);
        LocalDateTime today = LocalDateTime.now();
        List<Object[]> result = applicationRepository.countStatusApplicationsByMonth(oneYearAgo, today,ApplicationStatus.HIRED);
        List<MonthlyHiringStatDto> response = new ArrayList<>();
        for(Object[] row : result){
            int month = row[0] != null ? ((Number)row[0]).intValue() : 0;
            long count = row[1] != null ? ((Number)row[1]).longValue() : 0L;
            MonthlyHiringStatDto dto = new MonthlyHiringStatDto();
            dto.setMonth(month);
            dto.setHires(count);
            response.add(dto);
        }
        return response;
    }

    @Override 
    public List<PositionAnalyticsDto> buildPositionAnalytics() {
        Pageable page = PageRequest.of(0,10);
        Page<Object[]> result = positionRepository.findPositionAnalysis(page);
        List<PositionAnalyticsDto> response = new ArrayList<>();
        for(Object[] row : result){
            PositionAnalyticsDto dto = new PositionAnalyticsDto();
            Integer positionId = row[0] != null ? ((Number)row[0]).intValue() : null;
            dto.setPositionId(positionId);
            dto.setTitle(row[1] != null ? row[1].toString() : null);
            dto.setTotalApplications(row[2] != null ? ((Number)row[2]).longValue() : 0L);
            dto.setShortlisted(row[3] != null ? ((Number)row[3]).longValue() : 0L);
            dto.setSelected(row[4] != null ? ((Number)row[4]).longValue() : 0L);
            response.add(dto);
        }
        return response;
    }

    @Override 
    public RecruitmentFunnelStatDto buildRecruitmentFunnelStat() {
        RecruitmentFunnelStatDto dto = new RecruitmentFunnelStatDto();
        long totalApplication = applicationRepository.count();
        long shortListed = applicationRepository.countByIsShortlistedTrue();
        long selected = applicationRepository.countByApplicationStatus(ApplicationStatus.HIRED);

        dto.setApplications(totalApplication);
        dto.setShortlisted(shortListed);
        dto.setSelected(selected);

        return dto;
    }

    @Override
    public  List<DegreeCountDto> buildTopDegrees() {
        Pageable page = PageRequest.of(0,10);
        Page<Object[]> result = candidateEducationRepository.findTopDegrees(page);
        List<DegreeCountDto> response = new ArrayList<>();
        for(Object[] row : result){
            DegreeCountDto dto = new DegreeCountDto();
            dto.setDegree(row[0] != null ? row[0].toString() : "UNKNOWN");
            dto.setCount(row[1] != null ? ((Number)row[1]).longValue() : 0L);
            response.add(dto);
        }
        return response;
    }

    @Override 
    public  List<InterviewOutcomeStatDto> buildInterviewOutcomeStats() {
        List<Object[]> response = interviewRepository.countInterviewsByStatus();

        List<InterviewOutcomeStatDto> result = new ArrayList<>();
        Map<InterviewStatus, Long> statusCountMap = response.stream().collect(
                Collectors.toMap(
                        row -> row[0] != null ? InterviewStatus.valueOf(row[0].toString()) : null,
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L
                )
        );
        for (InterviewStatus status : InterviewStatus.values()) {
            InterviewOutcomeStatDto dto = new InterviewOutcomeStatDto();
            dto.setOutcome(status);
            dto.setCount(statusCountMap.getOrDefault(status, 0L));
            result.add(dto);
        }
        return result;
    }

    @Override 
    public  List<TopCandidateByExperienceDto> buildTopCandidatesByExperience() {
        List<CandidateModel> candidates = candidateRepository.findTop10ByOrderByCandidateTotalExperienceInYearsDesc();
        return candidates.stream().map(this::mapCandidateToTopExperienceDto).collect(Collectors.toList());

    }

    public  TopCandidateByExperienceDto  mapCandidateToTopExperienceDto(CandidateModel candidateModel) {
        TopCandidateByExperienceDto dto = new TopCandidateByExperienceDto();
        dto.setCandidateId(candidateModel.getCandidateId());
        dto.setName(candidateModel.getCandidateFirstName());
        dto.setEmail(candidateModel.getUser().getUserEmail());
        dto.setExperienceYears(candidateModel.getCandidateTotalExperienceInYears());
        dto.setPrimarySkills(candidateModel.getCandidateSkills().stream().map(
                skill -> skill.getSkill().getSkill()
        ).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public  ExperienceDistributionDto buildExperienceDistribution() {
        Object[] buckets = candidateRepository.getExperienceDistributionBuckets().getFirst();
        ExperienceDistributionDto dto = new ExperienceDistributionDto();
        if (buckets != null && buckets.length == 4) {
            dto.setLessThanOneYear(buckets[0] != null ? ((Number) buckets[0]).longValue() : 0L);
            dto.setOneToThreeYears(buckets[1] != null ? ((Number) buckets[1]).longValue() : 0L);
            dto.setThreeToFiveYears(buckets[2] != null ? ((Number) buckets[2]).longValue() : 0L);
            dto.setMoreThanFiveYears(buckets[3] != null ? ((Number) buckets[3]).longValue() : 0L);
        }
        return dto;
    }

    @Override 
    public  List<SkillCountDto> buildTopSkills() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Object[]> raw = candidateSkillRepository.findTopSkills(pageable);
        List<SkillCountDto> result = new ArrayList<>();
        for (Object[] row : raw) {
            SkillCountDto dto = new SkillCountDto();
            dto.setSkill(row[0] != null ? row[0].toString() : null);
            dto.setCount(row[1] != null ? ((Number) row[1]).longValue() : 0L);
            result.add(dto);
        }
        return result;
    }

    @Override 
    public  List<DailyApplicationsCountDto> buildApplicationsPerDay(int days) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<Object[]> raw = applicationRepository.countApplicationsByDateRange(start, end);
        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] row : raw) {
            LocalDate date = row[0] instanceof LocalDate
                    ? (LocalDate) row[0]
                    : ((java.sql.Date) row[0]).toLocalDate();
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            map.put(date, count);
        }

        List<DailyApplicationsCountDto> result = new ArrayList<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(today)) {
            DailyApplicationsCountDto dto = new DailyApplicationsCountDto();
            dto.setDate(cursor);
            dto.setCount(map.getOrDefault(cursor, 0L));
            result.add(dto);
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    @Override 
    public  List<UpcomingInterviewDto> buildUpcomingInterviews() {
        List<InterviewModel> interviews = interviewRepository.findUpcomingInterviews(LocalDate.now(),LocalTime.now());
        return interviews.stream().limit(10).map(this::convertor).collect(Collectors.toList());
    }


    @Override 
    public  List<RecentCandidateDto> buildRecentCandidates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CandidateModel> candidates = candidateRepository.findRecentCandidates(pageable);
        return candidates.stream().map(this::convertor).collect(Collectors.toList());
    }

    @Override 
    public  List<ApplicationStatusCountDto> buildApplicationStatusCounts() {
        List<Object[]> raw = applicationRepository.countApplicationsByStatus();
        List<ApplicationStatusCountDto> result = new ArrayList<>();
        Map<ApplicationStatus, Long> statusCountMap = raw.stream().collect(
                Collectors.toMap(
                        row -> row[0] != null ? ApplicationStatus.valueOf(row[0].toString()) : null,
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L
                )
        );
        for (ApplicationStatus status : ApplicationStatus.values()) {
            ApplicationStatusCountDto dto = new ApplicationStatusCountDto();
            dto.setApplicationStatus(status);
            dto.setCount(statusCountMap.getOrDefault(status, 0L));
            result.add(dto);
        }
        return result;
    }

    @Override 
    public  AdminSummaryStatsDto buildAdminSummaryStats() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long totalCandidates = candidateRepository.count();
        long newCandidatesToday = candidateRepository.countByCreatedAtBetween(startOfDay, endOfDay);
        long totalApplications = applicationRepository.count();
        long totalUsers = userRepository.count();
        long totalRequests = registerRepository.count();
        long upcomingInterviewsCount = interviewRepository.countUpcomingInterviews(today, currentTime);
        long totalOpenPositions = positionRepository.countActivePosition();
        long totalUniversities = universityRepository.count();
        long totalSkills = skillRepository.count();
        long totalDegrees = degreeRepository.count();

        AdminSummaryStatsDto dto = new AdminSummaryStatsDto();
        dto.setTotalCandidates(totalCandidates);
        dto.setTotalRequests(totalRequests);
        dto.setNewCandidatesToday(newCandidatesToday);
        dto.setTotalApplications(totalApplications);
        dto.setTotalUsers(totalUsers);
        dto.setTotalDegrees(totalDegrees);
        dto.setTotalOpenPositions(totalOpenPositions);
        dto.setTotalUniversities(totalUniversities);
        dto.setTotalSkills(totalSkills);
        dto.setUpcomingInterviewsCount(upcomingInterviewsCount);
        return dto;
    }

    @Override
    public RecruiterSummaryStatsDto buildRecruiterSummaryStats(Integer recruiterId) {
        long totalPositions = positionRepository.countPositionByRecruiter(recruiterId);
        long activePositions = positionRepository.countActivePositionByRecruiter(recruiterId);
        long totalApplications = applicationRepository.countApplicationsByRecruiter(recruiterId);
        long interviewsScheduled = interviewRepository.countInterviewsForRecruiter(recruiterId);
        long candidatesSelected = applicationRepository.countByStatusForRecruiter(recruiterId, ApplicationStatus.HIRED);

        RecruiterSummaryStatsDto summary = new RecruiterSummaryStatsDto();
        summary.setTotalPositions(totalPositions);
        summary.setActivePositions(activePositions);
        summary.setTotalApplications(totalApplications);
        summary.setInterviewsScheduled(interviewsScheduled);
        summary.setCandidatesSelected(candidatesSelected);

        return summary;
    }

        @Override
        public List<RecruiterPositionOverviewDto> buildRecruiterPositionOverviews(Integer recruiterId) {
            Pageable page = PageRequest.of(0,10);
            Page<Object[]> rawData = positionRepository.getPositionsOverviewByRecruiter(recruiterId, page);

            List<RecruiterPositionOverviewDto> result = new ArrayList<>();

            for (Object[] row : rawData) {
                RecruiterPositionOverviewDto dto = new RecruiterPositionOverviewDto();
                Integer positionId = row[0] != null ? ((Number) row[0]).intValue() : null;
                dto.setPositionId(positionId);
                dto.setTitle(row[1] != null ? row[1].toString() : null);
                dto.setStatus(row[2] != null ? row[2].toString() : null);
                dto.setApplications(row[3] != null ? ((Number) row[3]).longValue() : 0L);
                dto.setShortlisted(row[4] != null ? ((Number) row[4]).longValue() : 0L);
                dto.setSelected(row[5] != null ? ((Number) row[5]).longValue() : 0L);
                dto.setRejected(row[6] != null ? ((Number) row[6]).longValue() : 0L);
                dto.setCreatedAt(row[7] != null ? ((LocalDateTime) row[7]) : null);
                result.add(dto);
            }

            return result;
        }

    @Override
    public List<ApplicationStatusCountDto> buildApplicationStatusCounts(Integer recruiterId) {
        List<Object[]> raw = applicationRepository.countApplicationsStatusByRecruiter(recruiterId);
        List<ApplicationStatusCountDto> result = new ArrayList<>();
        Map<ApplicationStatus, Long> statusCountMap = raw.stream().collect(
                Collectors.toMap(
                        row -> row[0] != null ? ApplicationStatus.valueOf(row[0].toString()) : null,
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L
                )
        );
        for (ApplicationStatus status : ApplicationStatus.values()) {
            ApplicationStatusCountDto dto = new ApplicationStatusCountDto();
            dto.setApplicationStatus(status);
            dto.setCount(statusCountMap.getOrDefault(status, 0L));
            result.add(dto);
        }
        return result;
    }


    @Override
    public List<DailyApplicationsCountDto> buildApplicationsPerDay(Integer recruiterId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<Object[]> raw = applicationRepository.countApplicationsByDateRangeAndRecruiter(recruiterId, start, end);

        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] row : raw) {
            LocalDate date = row[0] instanceof LocalDate
                    ? (LocalDate) row[0]
                    : ((java.sql.Date) row[0]).toLocalDate();
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            map.put(date, count);
        }

        List<DailyApplicationsCountDto> result = new ArrayList<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(today)) {
            DailyApplicationsCountDto dto = new DailyApplicationsCountDto();
            dto.setDate(cursor);
            dto.setCount(map.getOrDefault(cursor, 0L));
            result.add(dto);
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    @Override
    public List<RecruiterRecentApplicationDto> buildRecruiterRecentApplications(Integer recruiterId) {
        List<Object[]> rawData = applicationRepository.findRecentApplicationsByRecruiter(recruiterId, PageRequest.of(0, 10));

        List<RecruiterRecentApplicationDto> result = new ArrayList<>();

        for (Object[] row : rawData) {
            RecruiterRecentApplicationDto dto = new RecruiterRecentApplicationDto();
            dto.setApplicationId((Integer) row[0]);
            dto.setCandidateName((String) row[1]);
            dto.setPositionTitle((String) row[2]);
            dto.setAppliedDate(((java.sql.Date) row[3]).toLocalDate());
            dto.setStatus(((ApplicationStatus) row[4]).toString());
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<UpcomingInterviewDto> buildUpcomingInterviews(Integer recruiterId) {
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.now();

        List<InterviewModel> rawData = interviewRepository.findUpcomingInterviewsByRecruiter(recruiterId, today,time);

        List<UpcomingInterviewDto> result = new ArrayList<>();

        for (InterviewModel row : rawData) {
            UpcomingInterviewDto dto = new UpcomingInterviewDto();
            dto.setInterviewId(row.getInterviewId());
            dto.setCandidateName(row.getRound().getApplication().getCandidate().getUser().getUsername());
            dto.setInterviewerName(row.getInterviewers().
                    stream().
                    map(i -> i.getInterviewer().getUsername()).collect(Collectors.joining(", ")));
            dto.setInterviewDate(row.getInterviewDate());
            dto.setInterviewTime(row.getInterviewTime());
            dto.setMeetingLink(row.getInterviewLink());
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<PositionPerformanceDto> buildPositionPerformance(Integer recruiterId) {
        List<Object[]> rawData = positionRepository.getPositionPerformanceMetrics(recruiterId);

        List<PositionPerformanceDto> result = new ArrayList<>();

        for (Object[] row : rawData) {

            PositionPerformanceDto dto = new PositionPerformanceDto();
            dto.setPositionId((Integer) row[0]);
            dto.setPositionTitle((String) row[1]);
            dto.setApplications((Long) row[2]);
            dto.setShortlistRate((Double) row[3]);
            dto.setMappedRate((Double) row[4]);
            dto.setSelectionRate((Double) row[5]);
            dto.setRejectionRate((Double) row[6]);
            result.add(dto);
        }

        return result;
    }

    @Override
    public ReviewerSummaryStatsDto buildReviewerSummaryStats(Integer reviewerId) {
        long totalApplications = applicationRepository.count();
        long shortlistedApplications = applicationRepository.countShortlistedByReviewer(reviewerId);
        long pendingApplications = applicationRepository.countPendingApplications();
        long reviewedToday = applicationRepository.countReviewedTodayByReviewer(reviewerId);

        ReviewerSummaryStatsDto dto = new ReviewerSummaryStatsDto();
        dto.setTotalApplications(totalApplications);
        dto.setShortlistedApplications(shortlistedApplications);
        dto.setPendingApplications(pendingApplications);
        dto.setReviewedToday(reviewedToday);

        return dto;
    }

    @Override
    public List<ReviewerApplicationDto> buildRecentApplications() {

        Pageable page = PageRequest.of(0,10);
        List<Object[]> raw = applicationRepository.findRecentApplications(page);
        List<ReviewerApplicationDto> response = new ArrayList<>();
        for(Object[] row : raw){
            ReviewerApplicationDto dto = new ReviewerApplicationDto();
            dto.setApplicationId(row[0] != null ? (Integer) row[0] : null);
            dto.setCandidateName(row[1] != null ? (String) row[1] : null);
            dto.setCandidateExperience(row[2] != null ? (Integer) row[2] : null);
            dto.setPositionTitle(row[3] != null ? (String) row[3] : null);
            dto.setAppliedDate(row[4] != null ? ((Date) row[4]).toLocalDate() : null);
            dto.setStatus(row[5] != null ? ((ApplicationStatus) row[5]).toString() : null);

            response.add(dto);
        }
        return response;
    }

    @Override
    public List<ReviewerApplicationDto> buildPendingReviewApplications() {
        List<Object[]> raw = applicationRepository.findPendingReviewApplications();
        List<ReviewerApplicationDto> response = new ArrayList<>();
        for(Object[] row : raw){
            ReviewerApplicationDto dto = new ReviewerApplicationDto();
            dto.setApplicationId(row[0] != null ? (Integer) row[0] : null);
            dto.setCandidateName(row[1] != null ? (String) row[1] : null);
            dto.setCandidateExperience(row[2] != null ? (Integer) row[2] : null);
            dto.setPositionTitle(row[3] != null ? (String) row[3] : null);
            dto.setAppliedDate(row[4] != null ? ((Date) row[4]).toLocalDate() : null);
            dto.setStatus(row[5] != null ? ((ApplicationStatus) row[5]).toString(): null);

            response.add(dto);
        }
        return response;
    }

    @Override
    public List<ReviewerApplicationDto> buildReviewerShortlistedApplications(Integer reviewerId) {
        Pageable page = PageRequest.of(0,10);
        Page<Object[]> raw = applicationRepository.findApplicationShortlistedByReviewer(reviewerId,page);
        List<ReviewerApplicationDto> response = new ArrayList<>();
        for(Object[] row : raw){
            ReviewerApplicationDto dto = new ReviewerApplicationDto();
            dto.setApplicationId(row[0] != null ? (Integer) row[0] : null);
            dto.setCandidateName(row[1] != null ? (String) row[1] : null);
            dto.setCandidateExperience(row[2] != null ? (Integer) row[2] : null);
            dto.setPositionTitle(row[3] != null ? (String) row[3] : null);
            dto.setAppliedDate(row[4] != null ? ((Date) row[4]).toLocalDate() : null);
            dto.setStatus(row[5] != null ? ((ApplicationStatus) row[5]).toString() : null);

            response.add(dto);
        }
        return response;
    }

    @Override
    public InterviewerSummaryStatsDto buildInterviewerSummaryStats(Integer interviewerId) {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        InterviewerSummaryStatsDto dto = new InterviewerSummaryStatsDto();
        long totalAssignedInterviews = interviewRepository.countInterviewAssigned(interviewerId);
        long upcomingInterviews = interviewRepository.upcomingInterviewsByInterviewer(interviewerId,today,now);
        long interviewsToday = interviewRepository.countTodayInterviewByInterviewer(interviewerId,today);
        long completedInterview = interviewRepository.countCompleteInterviewByInterviewer(interviewerId);
        long pendingFeedbackInterview = interviewRepository.countPendingFeedbackInterviewByInterviewer(interviewerId);

        dto.setTotalAssignedInterviews(totalAssignedInterviews);
        dto.setUpcomingInterviews(upcomingInterviews);
        dto.setInterviewsToday(interviewsToday);
        dto.setCompletedInterviews(completedInterview);
        dto.setPendingFeedbackCount(pendingFeedbackInterview);

        return dto;
    }

    @Override
    public List<InterviewerInterviewDto> buildTodaysInterviews(Integer interviewerId) {
        Pageable page = PageRequest.of(0,10);
        LocalDate today = LocalDate.now();
        Page<Object[]> raw = interviewRepository.findTodayInterviewsByInterviewer(interviewerId,today,page);

        List<InterviewerInterviewDto> response = new ArrayList<>();

        for(Object[] row : raw){
            InterviewerInterviewDto dto = new InterviewerInterviewDto();
            Integer interviewId = row[0] != null ? (Integer) row[0] : null;
            String candidateName = row[1] != null ? (String) row[1] : null;
            String positionTitle = row[2] != null ? (String) row[2] : null;
            LocalDate interviewDate = row[3] != null ? (LocalDate)row[3] : null;
            LocalTime interviewTime = row[4] != null ? (LocalTime) row[4] : null;
            String interviewType = row[5] != null ? ((RoundType)row[5]).toString() : null;
            String interviewStatus = row[6] != null ? ((InterviewStatus)row[6]).toString() : null;
            Boolean isFeedbackGiven = row[7] != null ? (Boolean)row[7] : null;

            dto.setInterviewId(interviewId);
            dto.setCandidateName(candidateName);
            dto.setPositionTitle(positionTitle);
            dto.setInterviewDate(interviewDate);
            dto.setInterviewTime(interviewTime);
            dto.setInterviewType(interviewType);
            dto.setInterviewStatus(interviewStatus);
            dto.setFeedbackSubmitted(isFeedbackGiven);

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<InterviewerInterviewDto> buildUpcomingInterview(Integer interviewerId) {
        Pageable page = PageRequest.of(0,10);
        LocalDate today = LocalDate.now();
        LocalDate future = LocalDate.now().plusDays(10);
        Page<Object[]> raw = interviewRepository.findUpcomingNDaysInterviewsByInterviewer(interviewerId,today,future,page);

        List<InterviewerInterviewDto> response = new ArrayList<>();

        for(Object[] row : raw){
            InterviewerInterviewDto dto = new InterviewerInterviewDto();
            Integer interviewId = row[0] != null ? (Integer) row[0] : null;
            String candidateName = row[1] != null ? (String) row[1] : null;
            String positionTitle = row[2] != null ? (String) row[2] : null;
            LocalDate interviewDate = row[3] != null ? (LocalDate)row[3] : null;
            LocalTime interviewTime = row[4] != null ? (LocalTime) row[4]: null;
            String interviewType = row[5] != null ? ((RoundType)row[5]).toString() : null;
            String interviewStatus = row[6] != null ? ((InterviewStatus)row[6]).toString() : null;
            Boolean isFeedbackGiven = row[7] != null ? (Boolean)row[7] : null;

            dto.setInterviewId(interviewId);
            dto.setCandidateName(candidateName);
            dto.setPositionTitle(positionTitle);
            dto.setInterviewDate(interviewDate);
            dto.setInterviewTime(interviewTime);
            dto.setInterviewType(interviewType);
            dto.setInterviewStatus(interviewStatus);
            dto.setFeedbackSubmitted(isFeedbackGiven);

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<InterviewerInterviewDto> buildPendingFeedbackInterviews(Integer interviewerId) {
        Pageable page = PageRequest.of(0,10);
        Page<Object[]> raw = interviewRepository.findPendingFeedbackInterviewsByInterviewer(interviewerId,page);

        List<InterviewerInterviewDto> response = new ArrayList<>();

        for(Object[] row : raw){
            InterviewerInterviewDto dto = new InterviewerInterviewDto();
            Integer interviewId = row[0] != null ? (Integer) row[0] : null;
            String candidateName = row[1] != null ? (String) row[1] : null;
            String positionTitle = row[2] != null ? (String) row[2] : null;
            LocalDate interviewDate = row[3] != null ? (LocalDate)row[3] : null;
            LocalTime interviewTime = row[4] != null ? (LocalTime) row[4] : null;
            String interviewType = row[5] != null ? ((RoundType)row[5]).toString() : null;
            String interviewStatus = row[6] != null ? ((InterviewStatus)row[6]).toString() : null;
            Boolean isFeedbackGiven = row[7] != null ? (Boolean)row[7] : null;

            dto.setInterviewId(interviewId);
            dto.setCandidateName(candidateName);
            dto.setPositionTitle(positionTitle);
            dto.setInterviewDate(interviewDate);
            dto.setInterviewTime(interviewTime);
            dto.setInterviewType(interviewType);
            dto.setInterviewStatus(interviewStatus);
            dto.setFeedbackSubmitted(isFeedbackGiven);

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<InterviewerInterviewDto> buildCompleteInterviews(Integer interviewerId) {
        Pageable page = PageRequest.of(0,10);
        Page<Object[]> raw = interviewRepository.findCompletedInterviewsByInterviewer(interviewerId,page);

        List<InterviewerInterviewDto> response = new ArrayList<>();

        for(Object[] row : raw){
            InterviewerInterviewDto dto = new InterviewerInterviewDto();
            Integer interviewId = row[0] != null ? (Integer) row[0] : null;
            String candidateName = row[1] != null ? (String) row[1] : null;
            String positionTitle = row[2] != null ? (String) row[2] : null;
            LocalDate interviewDate = row[3] != null ? (LocalDate)row[3] : null;
            LocalTime interviewTime = row[4] != null ? (LocalTime) row[4] : null;
            String interviewType = row[5] != null ? ((RoundType)row[5]).toString() : null;
            String interviewStatus = row[6] != null ? ((InterviewStatus)row[6]).toString() : null;
            Boolean isFeedbackGiven = row[7] != null ? (Boolean)row[7] : null;

            dto.setInterviewId(interviewId);
            dto.setCandidateName(candidateName);
            dto.setPositionTitle(positionTitle);
            dto.setInterviewDate(interviewDate);
            dto.setInterviewTime(interviewTime);
            dto.setInterviewType(interviewType);
            dto.setInterviewStatus(interviewStatus);
            dto.setFeedbackSubmitted(isFeedbackGiven);

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<InterviewStatusCountDto> buildInterviewStats(Integer interviewerId) {
        List<Object[]> raw = interviewRepository.countInterviewStatusByInterviewer(interviewerId);
        Map<InterviewStatus, Long> map = raw.stream().collect(
                Collectors.toMap(
                        row -> row[0] != null ? InterviewStatus.valueOf(row[0].toString()) : null,
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L
                )
        );

        List<InterviewStatusCountDto> response = new ArrayList<>();

        for(InterviewStatus status : InterviewStatus.values()){
            InterviewStatusCountDto dto = new InterviewStatusCountDto();
            dto.setInterviewStatus(status);
            dto.setCount(map.getOrDefault(status,0L));
            response.add(dto);
        }
        return response;
    }

    @Override
    public FeedbackStatusStatDto buildFeedbackStatus(Integer interviewerId) {
        FeedbackStatusStatDto response = new FeedbackStatusStatDto();

        long totalCompleteInterviews = interviewRepository.countCompleteInterviewByInterviewer(interviewerId);
        long pendingFeedbackInterviews = interviewRepository.countPendingFeedbackInterviewByInterviewer(interviewerId);

        response.setFeedbackGiven(totalCompleteInterviews - pendingFeedbackInterviews);
        response.setFeedbackPending(pendingFeedbackInterviews);

        return response;
    }

    @Override
    public List<DailyInterviewCountDto> buildNextInterviewPerDay(Integer interviewerId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(days-1);

        List<Object[]> raw = interviewRepository.countInterviewsPerDay(interviewerId,today,end);
        Map<LocalDate,Long> map = raw.stream().collect(
                Collectors.toMap(
                        row -> row[0] != null ? (LocalDate)row[0] : null,
                        row -> row[1] != null ? ((Number)row[1]).longValue() : 0L
                )
        );
        List<DailyInterviewCountDto> result = new ArrayList<>();
        LocalDate cursor = today;
        while (!cursor.isAfter(end)) {
            DailyInterviewCountDto dto = new DailyInterviewCountDto();
            dto.setDate(cursor);
            dto.setInterviewCount(map.getOrDefault(cursor,0L));
            result.add(dto);
            cursor = cursor.plusDays(1);
        }

        return result;
    }

    @Override
    public List<DailyInterviewCountDto> buildPreviousInterviewPerDay(Integer interviewerId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days);
        LocalDate end = today.minusDays(1);

        List<Object[]> raw = interviewRepository.countInterviewsPerDay(interviewerId,start,end);
        Map<LocalDate,Long> map = raw.stream().collect(
                Collectors.toMap(
                        row -> row[0] != null ? (LocalDate)row[0] : null,
                        row -> row[1] != null ? ((Number)row[1]).longValue() : 0L
                )
        );
        List<DailyInterviewCountDto> result = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            DailyInterviewCountDto dto = new DailyInterviewCountDto();
            dto.setDate(cursor);
            dto.setInterviewCount(map.getOrDefault(cursor,0L));
            result.add(dto);
            cursor = cursor.plusDays(1);
        }

        return result;
    }

    public  UpcomingInterviewDto convertor(InterviewModel model) {
        UpcomingInterviewDto dto = new UpcomingInterviewDto();
        dto.setInterviewId(model.getInterviewId());
        dto.setCandidateName(model.getRound().getApplication().getCandidate().getUser().getUsername());
        String interviewerNames = model.getInterviewers().stream()
                .map(interviewer -> interviewer.getInterviewer().getUsername())
                .collect(Collectors.joining(", "));
        dto.setInterviewerName(interviewerNames);
        dto.setInterviewDate(model.getInterviewDate());
        dto.setInterviewTime(model.getInterviewTime());
        dto.setMeetingLink(model.getInterviewLink());
        return dto;
    }

    public  RecentCandidateDto convertor(CandidateModel candidateModel) {
        RecentCandidateDto dto = new RecentCandidateDto();
        dto.setCandidateId(candidateModel.getCandidateId());
        dto.setCandidateName(candidateModel.getUser().getUsername());
        dto.setCandidateEmail(candidateModel.getUser().getUserEmail());
        dto.setCandidatePhoneNumber(candidateModel.getCandidatePhoneNumber());
        dto.setPrimarySkills(candidateModel.getCandidateSkills().stream().map(
                skill -> skill.getSkill().getSkill()
        ).collect(Collectors.toList()));
        dto.setExperienceYears(candidateModel.getCandidateTotalExperienceInYears());
        dto.setCreatedAt(candidateModel.getCreatedAt());
        return dto;
    }

}
