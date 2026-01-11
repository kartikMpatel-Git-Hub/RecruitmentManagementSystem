package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ModelServiceInterface {

    ApplicationModel addApplication(ApplicationModel application);
    ApplicationModel getApplication(Integer id);
    ApplicationStatusModel addApplicationStatus(ApplicationStatusModel applicationStatus);
    ApplicationStatusModel getApplicationStatus(Integer id);
    Page<ApplicationModel> getPositionApplicationByStatus(Integer positionId, ApplicationStatus applicationStatus, Pageable pageable);
    boolean existApplication(Integer candidateId, Integer positionId);
    Page<ApplicationModel> getShortlistedApplications(Pageable pageable);
    Page<ApplicationModel> getRecruiterShortlistedApplications(Integer recruiterId, Pageable pageable);
    Page<ApplicationModel> getPositionShortlistedApplications(Integer positionId, Pageable pageable);
    Page<ApplicationModel> getCandidateShortlistedApplications(Integer candidateId, Pageable pageable);
    Page<ApplicationModel> getReviewerShortlistedApplications(Integer reviewerId, Pageable pageable);
    Page<ApplicationModel> getPositionReviewerShortlistedApplications(Integer positionId, Integer reviewerId, Pageable pageable);
    Page<ApplicationModel> getPositionApplications(Integer positionId, Pageable pageable);
    Page<ApplicationModel> getCandidateApplications(Integer candidateId, Pageable pageable);
    Page<ApplicationModel> getRecruiterApplications(Integer recruiterId, Pageable pageable);
    Page<ApplicationModel> getAllApplications(Pageable pageable);
    List<Integer> getCandidateApplicationIds(Integer candidateId);
    void removeApplication(ApplicationModel existingApplication);
    Long countApplications();
    long countShortlistedApplications();
    List<Object[]> countStatusApplicationByMonth(LocalDateTime oneYearAgo, LocalDateTime today, ApplicationStatus applicationStatus);
    long countApplicationByStatus(ApplicationStatus applicationStatus);

    BulkUploadJob addBulkUploadJob(BulkUploadJob job);
    BulkUploadJob getBulkUploadJob(Integer id);
    BulkUploadRowResult addBulkUploadRowResult(BulkUploadRowResult rowResult);
    BulkUploadRowResult getBulkUploadRowResult(Integer id);
    List<BulkUploadRowResult> getAllBulkRowResultByJob(BulkUploadJob job);
    Page<BulkUploadJob> getAllBulkUploadJobs(Pageable pageable);

    CandidateModel addCandidate(CandidateModel candidate);
    CandidateModel getCandidate(Integer id);
    CandidateEducationModel addCandidateEducation(CandidateEducationModel education);
    CandidateEducationModel getCandidateEducation(Integer id);
    Page<CandidateEducationModel> getAllCandidateEducations(Pageable pageable);
    void removeCandidateEducation(CandidateEducationModel existingCandidateEducation);
    Page<CandidateEducationModel> getAllCandidateEducationByUniversity(Integer universityId, Pageable pageable);
    Page<CandidateEducationModel> getAllCandidateEducationByCandidate(Integer candidateId, Pageable pageable);
    Page<CandidateEducationModel> getAllCandidateEducationByDegree(Integer degreeId, Pageable pageable);
    CandidateSkillModel addCandidateSkill(CandidateSkillModel skill);
    CandidateSkillModel getCandidateSkill(Integer id);
    Page<CandidateSkillModel> getAllCandidateSkills(Pageable pageable);
    Page<CandidateSkillModel> getCandidateSkillByCandidate(Integer candidateId, Pageable pageable);
    Page<CandidateSkillModel> getCandidateSkillBySkill(Integer skillId, Pageable pageable);
    void removeCandidateSkill(CandidateSkillModel existingCandidateSkill);
    long countNewCandidateToday();
    List<CandidateModel> getAllCandidates();
    CandidateModel getCandidateByUserId(Integer userId);
    void removeCandidate(CandidateModel candidate);
    Page<CandidateModel> getAllCandidates(Pageable pageable);
    Long countCandidates();

    DegreeModel addDegree(DegreeModel degree);
    DegreeModel getDegree(Integer id);
    Page<DegreeModel> getAllDegrees(Pageable pageable);
    void removeDegree(DegreeModel degree);
    long countDegrees();
    Page<Object[]> getTopDegrees(Pageable page);

    DocumentModel addDocument(DocumentModel document);
    DocumentModel getDocument(Integer id);
    DocumentVerificationModel addDocumentVerification(DocumentVerificationModel documentVerification);
    DocumentVerificationModel getDocumentVerification(Integer id);

    InterviewerFeedbackModel addInterviewerFeedback(InterviewerFeedbackModel feedback);
    InterviewerFeedbackModel getInterviewerFeedback(Integer id);
    InterviewInterviewerModel addInterviewInterviewer(InterviewInterviewerModel interviewInterviewer);
    InterviewInterviewerModel getInterviewInterviewer(Integer id);
    InterviewModel addInterview(InterviewModel interview);
    InterviewModel getInterview(Integer id);

    PositionModel addPosition(PositionModel position);
    PositionModel getPosition(Integer id);
    PositionRequirementModel addPositionRequirement(PositionRequirementModel requirement);
    PositionRequirementModel getPositionRequirement(Integer id);
    PositionStatusModel addPositionStatus(PositionStatusModel positionStatus);
    PositionStatusModel getPositionStatus(Integer id);
    PositionRoundModel addPositionRound(PositionRoundModel positionRound);
    PositionRoundModel getPositionRound(Integer id);
    long countOpenPositions();
    Page<Object[]> getPositionAnalysis(Pageable page);

    RegisterModel addRegister(RegisterModel register);
    RegisterModel getRegister(Integer id);

    RoleModel addRole(RoleModel role);
    RoleModel getRole(Integer id);
    RoleModel getRole(String candidate);

    RoundModel addRound(RoundModel round);
    RoundModel getRound(Integer id);
    List<RoundModel> addAllRound(List<RoundModel> rounds);
    List<RoundModel> getApplicationRounds(Integer applicationId);
    Page<RoundModel> getRoundsByApplicationId(Integer applicationId, Pageable pageable);
    Page<RoundModel> getRoundsByCandidateId(Integer candidateId, Pageable pageable);
    void deleteRound(RoundModel round);

    SkillModel addSkill(SkillModel skill);
    SkillModel getSkill(Integer id);
    Page<SkillModel> findAllSkills(Pageable pageable);
    boolean existsSkill(String skill);
    SkillModel getSkill(String skill);
    List<SkillModel> getAllSkills();
    void deleteSkill(SkillModel skill);
    long countSkills();

    SkillRatingModel addSkillRating(SkillRatingModel skillRating);
    SkillRatingModel getSkillRating(Integer id);

    UniversityModel addUniversity(UniversityModel university);
    UniversityModel getUniversity(Integer id);
    UniversityModel getUniversity(String universityName);
    Page<UniversityModel> findAllUniversity(Pageable pageable);
    boolean existsByUniversity(String university);
    void removeUniversity(UniversityModel existingUniversity);
    long countUniversities();
    Page<Object[]> getTopUniversities(Pageable page);

    UserModel addUser(UserModel user);
    UserModel getUser(Integer id);
    UserModel getUser(String userName);
    UserModel getCurrentUser();
    Page<UserModel> getUsers(Pageable pageable);
    Page<UserModel> getCandidates(Pageable pageable);
    Page<UserModel> getNonCandidates(Pageable pageable);
    Page<UserModel> getInterviewers(Pageable pageable);
    void removeUser(UserModel user);
    boolean existedUserByUserName(String userName);
    boolean existsUserByEmail(String userEmail);
    Page<UserModel> getHrs(Pageable pageable);

    long countUpcomingInterviews(LocalDate date, LocalTime time);
    List<Object[]> countInterviewByStatus();

    Page<RegisterModel> getAllRequests(Pageable pageable);
    void removeRegister(RegisterModel registerRequest);
    long countRegisterRequest();

}
