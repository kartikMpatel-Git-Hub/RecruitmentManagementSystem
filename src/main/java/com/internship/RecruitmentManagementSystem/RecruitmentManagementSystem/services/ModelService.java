package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService implements ModelServiceInterface {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final BulkUploadJobRepository bulkUploadJobRepository;
    private final BulkUploadRowResultRepository bulkUploadRowResultRepository;
    private final CandidateRepository candidateRepository;
    private final CandidateEducationRepository candidateEducationRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final DegreeRepository degreeRepository;
    private final DocumentRepository documentRepository;
    private final DocumentVerificationRepository documentVerificationRepository;
    private final InterviewerFeedbackRepository interviewerFeedbackRepository;
    private final InterviewInterviewerRepository interviewInterviewerRepository;
    private final InterviewRepository interviewRepository;
    private final PositionRepository positionRepository;
    private final PositionRequirementRepository positionRequirementRepository;
    private final PositionStatusRepository positionStatusRepository;
    private final PositionRoundRepository positionRoundRepository;
    private final RegisterRepository registerRepository;
    private final RoleRepository roleRepository;
    private final RoundRepository roundRepository;
    private final SkillRepository skillRepository;
    private final SkillRatingRepository skillRatingRepository;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;


    @Override
    public ApplicationModel addApplication(ApplicationModel application) {
        return applicationRepository.save(application);
    }

    @Override
    public ApplicationModel getApplication(Integer id) {
        return applicationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Application", "id", id.toString())
        );
    }

    @Override
    public ApplicationStatusModel addApplicationStatus(ApplicationStatusModel applicationStatus) {
        return applicationStatusRepository.save(applicationStatus);
    }

    @Override
    public ApplicationStatusModel getApplicationStatus(Integer id) {
        return applicationStatusRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("ApplicationStatus", "id", id.toString())
        );
    }

    @Override
    public Page<ApplicationModel> getPositionApplicationByStatus(Integer positionId, ApplicationStatus applicationStatus, Pageable pageable) {
        return applicationRepository
                .findByApplicationStatusApplicationStatusAndPositionPositionId
                        (applicationStatus, positionId, pageable);
    }

    @Override
    public boolean existApplication(Integer candidateId, Integer positionId) {
        return applicationRepository.existsByCandidateCandidateIdAndPositionPositionId(candidateId, positionId);
    }

    @Override
    public Page<ApplicationModel> getShortlistedApplications(Pageable pageable) {
        return applicationRepository.findByIsShortlistedTrue(pageable);
    }

    @Override
    public Page<ApplicationModel> getRecruiterShortlistedApplications(Integer recruiterId, Pageable pageable) {
        return applicationRepository.findRecruiterShortlists(recruiterId, pageable);
    }

    @Override
    public Page<ApplicationModel> getPositionShortlistedApplications(Integer positionId, Pageable pageable) {
        return applicationRepository.findByIsShortlistedTrueAndPositionPositionId(positionId, pageable);
    }

    @Override
    public Page<ApplicationModel> getCandidateShortlistedApplications(Integer candidateId, Pageable pageable) {
        return applicationRepository.findByIsShortlistedTrueAndCandidateCandidateId(candidateId, pageable);
    }

    @Override
    public Page<ApplicationModel> getReviewerShortlistedApplications(Integer reviewerId, Pageable pageable) {
        return applicationRepository.findShortlistsByReviewer(reviewerId, pageable);
    }

    @Override
    public Page<ApplicationModel> getPositionReviewerShortlistedApplications(Integer positionId, Integer reviewerId, Pageable pageable) {
        return applicationRepository.findByShortlistedByPositionAndReviewer(positionId, reviewerId, pageable);
    }

    @Override
    public Page<ApplicationModel> getPositionApplications(Integer positionId, Pageable pageable) {
        return applicationRepository.findByPositionPositionId(positionId, pageable);
    }

    @Override
    public Page<ApplicationModel> getCandidateApplications(Integer candidateId, Pageable pageable) {
        return applicationRepository.findByCandidateCandidateId(candidateId, pageable);
    }

    @Override
    public Page<ApplicationModel> getRecruiterApplications(Integer recruiterId, Pageable pageable) {
        return applicationRepository.findRecruiterApplications(recruiterId, pageable);
    }

    @Override
    public Page<ApplicationModel> getAllApplications(Pageable pageable) {
        return applicationRepository.findAllApplications(pageable);
    }

    @Override
    public List<Integer> getCandidateApplicationIds(Integer candidateId) {
        return applicationRepository.findAppliedPositionIdsByCandidateId(candidateId);
    }

    @Override
    public void removeApplication(ApplicationModel existingApplication) {
        applicationRepository.delete(existingApplication);
    }

    @Override
    public Long countApplications() {
        return applicationRepository.count();
    }

    @Override
    public long countShortlistedApplications() {
        return applicationRepository.countByIsShortlistedTrue();
    }

    @Override
    public List<Object[]> countStatusApplicationByMonth(LocalDateTime oneYearAgo, LocalDateTime today, ApplicationStatus applicationStatus) {
        return applicationRepository.countStatusApplicationsByMonth(oneYearAgo,today,applicationStatus);
    }

    @Override
    public long countApplicationByStatus(ApplicationStatus applicationStatus) {
        return applicationRepository.countByApplicationStatus(applicationStatus);
    }

    @Override
    public BulkUploadJob addBulkUploadJob(BulkUploadJob job) {
        return bulkUploadJobRepository.save(job);
    }

    @Override
    public BulkUploadJob getBulkUploadJob(Integer id) {
        return bulkUploadJobRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("BulkUploadJob", "id", id.toString())
        );
    }

    @Override
    public BulkUploadRowResult addBulkUploadRowResult(BulkUploadRowResult rowResult) {
        return bulkUploadRowResultRepository.save(rowResult);
    }

    @Override
    public BulkUploadRowResult getBulkUploadRowResult(Integer id) {
        return bulkUploadRowResultRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("BulkUploadRowResult", "id", id.toString())
        );
    }

    @Override
    public List<BulkUploadRowResult> getAllBulkRowResultByJob(BulkUploadJob job) {
        return bulkUploadRowResultRepository.findByJob(job);
    }

    @Override
    public Page<BulkUploadJob> getAllBulkUploadJobs(Pageable pageable) {
        return bulkUploadJobRepository.findAll(pageable);
    }

    @Override
    public CandidateModel addCandidate(CandidateModel candidate) {
        return candidateRepository.save(candidate);
    }

    @Override
    public CandidateModel getCandidate(Integer id) {
        return candidateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Candidate", "id", id.toString())
        );
    }

    @Override
    public CandidateEducationModel addCandidateEducation(CandidateEducationModel education) {
        return candidateEducationRepository.save(education);
    }

    @Override
    public CandidateEducationModel getCandidateEducation(Integer id) {
        return candidateEducationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("CandidateEducation", "id", id.toString())
        );
    }

    @Override
    public Page<CandidateEducationModel> getAllCandidateEducations(Pageable pageable) {
        return candidateEducationRepository.findAll(pageable);
    }

    @Override
    public void removeCandidateEducation(CandidateEducationModel existingCandidateEducation) {
        candidateEducationRepository.delete(existingCandidateEducation);
    }

    @Override
    public Page<CandidateEducationModel> getAllCandidateEducationByUniversity(Integer universityId, Pageable pageable) {
        return candidateEducationRepository.findByUniversity_UniversityId(universityId,pageable);
    }

    @Override
    public Page<CandidateEducationModel> getAllCandidateEducationByCandidate(Integer candidateId, Pageable pageable) {
        return candidateEducationRepository.findByCandidate_CandidateId(candidateId,pageable);
    }

    @Override
    public Page<CandidateEducationModel> getAllCandidateEducationByDegree(Integer degreeId, Pageable pageable) {
        return candidateEducationRepository.findByDegree_DegreeId(degreeId,pageable);
    }

    @Override
    public CandidateSkillModel addCandidateSkill(CandidateSkillModel skill) {
        return candidateSkillRepository.save(skill);
    }

    @Override
    public CandidateSkillModel getCandidateSkill(Integer id) {
        return candidateSkillRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("CandidateSkill", "id", id.toString())
        );
    }

    @Override
    public Page<CandidateSkillModel> getAllCandidateSkills(Pageable pageable) {
        return candidateSkillRepository.findAll(pageable);
    }

    @Override
    public Page<CandidateSkillModel> getCandidateSkillByCandidate(Integer candidateId, Pageable pageable) {
        return candidateSkillRepository.findByCandidate_CandidateId(candidateId,pageable);
    }

    @Override
    public Page<CandidateSkillModel> getCandidateSkillBySkill(Integer skillId, Pageable pageable) {
        return candidateSkillRepository.findBySkill_SkillId(skillId,pageable);
    }

    @Override
    public void removeCandidateSkill(CandidateSkillModel existingCandidateSkill) {
        candidateSkillRepository.delete(existingCandidateSkill);
    }

    @Override
    public long countNewCandidateToday() {
        return candidateRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );
    }

    @Override
    public DegreeModel addDegree(DegreeModel degree) {
        return degreeRepository.save(degree);
    }

    @Override
    public DegreeModel getDegree(Integer id) {
        return degreeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Degree", "id", id.toString())
        );
    }

    @Override
    public Page<DegreeModel> getAllDegrees(Pageable pageable) {
        return degreeRepository.findAll(pageable);
    }

    @Override
    public void removeDegree(DegreeModel degree) {
        degreeRepository.delete(degree);
    }

    @Override
    public long countDegrees() {
        return degreeRepository.count();
    }

    @Override
    public Page<Object[]> getTopDegrees(Pageable page) {
        return degreeRepository.findTopSkills(page);
    }

    @Override
    public DocumentModel addDocument(DocumentModel document) {
        return documentRepository.save(document);
    }

    @Override
    public DocumentModel getDocument(Integer id) {
        return documentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Document", "id", id.toString())
        );
    }

    @Override
    public DocumentVerificationModel addDocumentVerification(DocumentVerificationModel documentVerification) {
        return documentVerificationRepository.save(documentVerification);
    }

    @Override
    public DocumentVerificationModel getDocumentVerification(Integer id) {
        return documentVerificationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("DocumentVerification", "id", id.toString())
        );
    }

    @Override
    public InterviewerFeedbackModel addInterviewerFeedback(InterviewerFeedbackModel feedback) {
        return interviewerFeedbackRepository.save(feedback);
    }

    @Override
    public InterviewerFeedbackModel getInterviewerFeedback(Integer id) {
        return interviewerFeedbackRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("InterviewerFeedback", "id", id.toString())
        );
    }

    @Override
    public InterviewInterviewerModel addInterviewInterviewer(InterviewInterviewerModel interviewInterviewer) {
        return interviewInterviewerRepository.save(interviewInterviewer);
    }

    @Override
    public InterviewInterviewerModel getInterviewInterviewer(Integer id) {
        return interviewInterviewerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("InterviewInterviewer", "id", id.toString())
        );
    }

    @Override
    public InterviewModel addInterview(InterviewModel interview) {
        return interviewRepository.save(interview);
    }

    @Override
    public InterviewModel getInterview(Integer id) {
        return interviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Interview", "id", id.toString())
        );
    }

    @Override
    public PositionModel addPosition(PositionModel position) {
        return positionRepository.save(position);
    }

    @Override
    public PositionModel getPosition(Integer id) {
        return positionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Position", "id", id.toString())
        );
    }

    @Override
    public PositionRequirementModel addPositionRequirement(PositionRequirementModel requirement) {
        return positionRequirementRepository.save(requirement);
    }

    @Override
    public PositionRequirementModel getPositionRequirement(Integer id) {
        return positionRequirementRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PositionRequirement", "id", id.toString())
        );
    }

    @Override
    public PositionStatusModel addPositionStatus(PositionStatusModel positionStatus) {
        return positionStatusRepository.save(positionStatus);
    }

    @Override
    public PositionStatusModel getPositionStatus(Integer id) {
        return positionStatusRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PositionStatus", "id", id.toString())
        );
    }

    @Override
    public PositionRoundModel addPositionRound(PositionRoundModel positionRound) {
        return positionRoundRepository.save(positionRound);
    }

    @Override
    public PositionRoundModel getPositionRound(Integer id) {
        return positionRoundRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PositionRound", "id", id.toString())
        );
    }

    @Override
    public long countOpenPositions() {
        return positionRepository.countActivePosition();
    }

    @Override
    public Page<Object[]> getPositionAnalysis(Pageable page) {
        return positionRepository.findPositionAnalysis(page);
    }

    @Override
    public RegisterModel addRegister(RegisterModel register) {
        return registerRepository.save(register);
    }

    @Override
    public RegisterModel getRegister(Integer id) {
        return registerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Register", "id", id.toString())
        );
    }

    @Override
    public RoleModel addRole(RoleModel role) {
        return roleRepository.save(role);
    }

    @Override
    public RoleModel getRole(Integer id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id.toString())
        );
    }

    @Override
    public RoleModel getRole(String role) {
        return roleRepository.findByRole(role).orElseThrow(
                () -> new ResourceNotFoundException("Role", "role", role)
        );
    }

    @Override
    public RoundModel addRound(RoundModel round) {
        return roundRepository.save(round);
    }

    @Override
    public RoundModel getRound(Integer id) {
        return roundRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Round", "id", id.toString())
        );
    }

    @Override
    public List<RoundModel> addAllRound(List<RoundModel> rounds) {
        return roundRepository.saveAll(rounds);
    }

    @Override
    public List<RoundModel> getApplicationRounds(Integer applicationId) {
        return roundRepository.findByApplicationApplicationId(applicationId);
    }

    @Override
    public Page<RoundModel> getRoundsByApplicationId(Integer applicationId, Pageable pageable) {
        return roundRepository.findByApplicationApplicationId(applicationId,pageable);
    }

    @Override
    public Page<RoundModel> getRoundsByCandidateId(Integer candidateId, Pageable pageable) {
        return roundRepository.findByApplicationCandidateCandidateId(candidateId,pageable);
    }

    @Override
    public void deleteRound(RoundModel round) {
        roundRepository.delete(round);
    }

    @Override
    public SkillModel addSkill(SkillModel skill) {
        return skillRepository.save(skill);
    }

    @Override
    public SkillModel getSkill(Integer id) {
        return skillRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Skill", "id", id.toString())
        );
    }

    @Override
    public Page<SkillModel> findAllSkills(Pageable pageable) {
        return skillRepository.findAll(pageable);
    }

    @Override
    public boolean existsSkill(String skill) {
        return skillRepository.existsBySkill(skill);
    }

    @Override
    public SkillModel getSkill(String skill) {
        return skillRepository.findBySkill(skill).orElseThrow(
                ()-> new ResourceNotFoundException("Skill","skill",skill)
        );
    }

    @Override
    public List<SkillModel> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public void deleteSkill(SkillModel skill) {
        skillRepository.delete(skill);
    }

    @Override
    public long countSkills() {
        return skillRepository.count();
    }

    @Override
    public SkillRatingModel addSkillRating(SkillRatingModel skillRating) {
        return skillRatingRepository.save(skillRating);
    }

    @Override
    public SkillRatingModel getSkillRating(Integer id) {
        return skillRatingRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("SkillRating", "id", id.toString())
        );
    }

    @Override
    public UniversityModel addUniversity(UniversityModel university) {
        return universityRepository.save(university);
    }

    @Override
    public UniversityModel getUniversity(Integer id) {
        return universityRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("University", "id", id.toString())
        );
    }

    @Override
    public UniversityModel getUniversity(String universityName) {
        return universityRepository.findByUniversity(universityName).orElseThrow(
                () -> new ResourceNotFoundException("University", "universityName", universityName)
        );
    }

    @Override
    public Page<UniversityModel> findAllUniversity(Pageable pageable) {
        return universityRepository.findAll(pageable);
    }

    @Override
    public boolean existsByUniversity(String university) {
        return universityRepository.existsByUniversity(university);
    }

    @Override
    public void removeUniversity(UniversityModel existingUniversity) {
        universityRepository.delete(existingUniversity);
    }

    @Override
    public long countUniversities() {
        return universityRepository.count();
    }

    @Override
    public Page<Object[]> getTopUniversities(Pageable page) {
        return universityRepository.findTopUniversities(page);
    }

    @Override
    public UserModel addUser(UserModel user) {
        return userRepository.save(user);
    }

    @Override
    public UserModel getUser(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString())
        );
    }

    @Override
    public UserModel getUser(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(
                ()-> new ResourceNotFoundException("User","userName",userName)
        );
    }

    @Override
    public UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Page<UserModel> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<UserModel> getCandidates(Pageable pageable) {
        return userRepository.findCandidate(pageable);
    }

    @Override
    public Page<UserModel> getNonCandidates(Pageable pageable) {
        return userRepository.findNonCandidate(pageable);
    }

    @Override
    public Page<UserModel> getInterviewers(Pageable pageable) {
        return userRepository.findInterviewers(pageable);
    }

    @Override
    public Page<UserModel> getHrs(Pageable pageable) {
        return userRepository.findHrs(pageable);
    }

    @Override
    public long countUpcomingInterviews(LocalDate date,LocalTime time) {
        return interviewRepository.countUpcomingInterviews(date, time);
    }

    @Override
    public List<Object[]> countInterviewByStatus() {
        return interviewRepository.countInterviewsByStatus();
    }

    @Override
    public void removeUser(UserModel user) {
        userRepository.delete(user);
    }

    @Override
    public boolean existedUserByUserName(String userName) {
        return userRepository.existsByUserName(userName) ||
                        registerRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsUserByEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail) ||
                    registerRepository.existsByUserEmail(userEmail);
    }

    @Override
    public List<CandidateModel> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public CandidateModel getCandidateByUserId(Integer userId) {
        return candidateRepository.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException("Candidate","userId",userId.toString())
        );
    }

    @Override
    public void removeCandidate(CandidateModel candidate) {
        candidateRepository.delete(candidate);
    }

    @Override
    public Page<CandidateModel> getAllCandidates(Pageable pageable) {
        return candidateRepository.findAll(pageable);
    }

    @Override
    public Long countCandidates() {
        return candidateRepository.count();
    }

    @Override
    public Page<RegisterModel> getAllRequests(Pageable pageable) {
        return registerRepository.findAll(pageable);
    }

    @Override
    public void removeRegister(RegisterModel registerRequest) {
        registerRepository.delete(registerRequest);
    }

    @Override
    public long countRegisterRequest() {
        return registerRepository.count();
    }

}
