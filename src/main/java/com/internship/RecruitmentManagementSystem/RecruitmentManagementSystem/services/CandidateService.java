package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.CandidateUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ProficiencyLevel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.CandidateRegistrationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CandidateService implements CandidateServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final Mapper mapper;
    private final FileService fileService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelServiceInterface modelService;
    private final PaginatedResponseCreator paginatedResponseCreator;
    private static final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    @CacheEvict(value = "candidateData", allEntries = true)
    public CandidateRegistrationResponse register(UserModel userModel) {
        logger.info("Registering new candidate for user email: {}", userModel.getUserEmail());
        CandidateModel newCandidate = new CandidateModel();
        newCandidate.setUser(userModel);
        CandidateModel candidate = modelService.addCandidate(newCandidate);
        logger.info("Candidate registered successfully with candidateId: {}", candidate.getCandidateId());

        return new CandidateRegistrationResponse(
                candidate.getCandidateId(),
                userModel.getUserId(),
                userModel.getUserEmail(),
                "ROLE_CANDIDATE"
        );
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateData", allEntries = true),
            @CacheEvict(value = "userCandidate", allEntries = true)
    })
    public CandidateResponseDto updateCandidate(MultipartFile resume, CandidateUpdateDto newCandidate, Integer candidateId) {
        logger.info("Updating candidate with ID: {}", candidateId);

        if(resume != null && !resume.isEmpty()) {
            String resumeUrl = saveFile(resume);
            if (resumeUrl != null) {
                newCandidate.setCandidateResumeUrl(resumeUrl);
                logger.info("Updated resume URL for candidate ID: {}", candidateId);
            } else {
                logger.warn("Failed to update resume for candidate ID: {}", candidateId);
            }
        }

        CandidateModel existingCandidate = modelService.getCandidate(candidateId);

        if (newCandidate.getCandidateFirstName() != null) existingCandidate.setCandidateFirstName(newCandidate.getCandidateFirstName());
        if (newCandidate.getCandidateMiddleName() != null) existingCandidate.setCandidateMiddleName(newCandidate.getCandidateMiddleName());
        if (newCandidate.getCandidateLastName() != null) existingCandidate.setCandidateLastName(newCandidate.getCandidateLastName());
        if (newCandidate.getCandidateGender() != null) existingCandidate.setCandidateGender(newCandidate.getCandidateGender());
        if (newCandidate.getCandidateDateOfBirth() != null) existingCandidate.setCandidateDateOfBirth(newCandidate.getCandidateDateOfBirth());
        if (newCandidate.getCandidateAddress() != null) existingCandidate.setCandidateAddress(newCandidate.getCandidateAddress());
        if (newCandidate.getCandidateCity() != null) existingCandidate.setCandidateCity(newCandidate.getCandidateCity());
        if (newCandidate.getCandidateState() != null) existingCandidate.setCandidateState(newCandidate.getCandidateState());
        if (newCandidate.getCandidateCountry() != null) existingCandidate.setCandidateCountry(newCandidate.getCandidateCountry());
        if (newCandidate.getCandidateZipCode() != null) existingCandidate.setCandidateZipCode(newCandidate.getCandidateZipCode());
        if (newCandidate.getCandidatePhoneNumber() != null) existingCandidate.setCandidatePhoneNumber(newCandidate.getCandidatePhoneNumber());
        if (newCandidate.getCandidateResumeUrl() != null) existingCandidate.setCandidateResumeUrl(newCandidate.getCandidateResumeUrl());
        if (newCandidate.getCandidateTotalExperienceInYears() != null) existingCandidate.setCandidateTotalExperienceInYears(newCandidate.getCandidateTotalExperienceInYears());

        CandidateModel updatedCandidate = modelService.addCandidate(existingCandidate);
        logger.info("Candidate updated successfully with ID: {}", candidateId);
        return mapper.toDto(updatedCandidate, CandidateResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateData", allEntries = true),
            @CacheEvict(value = "userCandidate", allEntries = true)
    })
    public Boolean deleteCandidate(Integer candidateId) {
        logger.info("Deleting candidate with ID: {}", candidateId);

        CandidateModel existingCandidate = modelService.getCandidate(candidateId);
        try {
            modelService.removeCandidate(existingCandidate);
            logger.info("Candidate deleted successfully with ID: {}", candidateId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete candidate with ID: {}", candidateId, e);
            return false;
        }
    }

    @Override
    @Cacheable(value = "candidateData", key = "'page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<CandidateResponseDto> getAllCandidates(int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching candidates - Page: {}, Size: {}, SortBy: {}, SortDir: {}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page,size,sortBy,sortDir);


        return paginatedResponseCreator
                .getPaginatedResponse(
                        modelService.getAllCandidates(pageable),
                        CandidateResponseDto.class);
    }

    @Override
    @Transactional
    @Cacheable(value = "userCandidate",key = "'id_' + #p0")
    public CandidateResponseDto getCandidate(Integer candidateId) {
        logger.info("Fetching candidate with ID: {}", candidateId);
        CandidateModel candidate = modelService.getCandidate(candidateId);
        logger.info("candidate fetched with ID: {}", candidateId);
        return mapper.toDto(candidate,CandidateResponseDto.class);
    }

    @Override
    @Transactional
    @Cacheable(value = "userCandidate",key = "'userId_' + #userId")
    public CandidateResponseDto getCandidateByUserId(Integer userId) {
        logger.info("Fetching candidate with userId: {}", userId);
        CandidateModel candidate = modelService.getCandidateByUserId(userId);
        logger.info("candidate fetched with userId: {}", userId);
        return mapper.toDto(candidate,CandidateResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "candidateData", allEntries = true),
            @CacheEvict(value = "userCandidate", allEntries = true)
    })
    public CandidateResponseDto updateCandidateSkills(Integer candidateId, List<Integer> skillIds) {
        logger.info("Updating skills for candidate with ID: {}", candidateId);

        CandidateModel existingCandidate = modelService.getCandidate(candidateId);

        List<SkillModel> skills = skillIds.stream().map(modelService::getSkill).toList();

        CandidateModel updatedCandidate = modelService.addCandidate(existingCandidate);
        logger.info("Updated skills for candidate with ID: {}", candidateId);

        return mapper.toDto(updatedCandidate, CandidateResponseDto.class);
    }

    @Override
    @Cacheable(value = "candidateData",key = "'candidate_count'")
    public Long countCandidates() {
        return modelService.countCandidates();
    }

    @Override
    @Transactional
    @CacheEvict(value = "candidateData", allEntries = true)
    public CandidateResponseDto processResume(MultipartFile resume) {
        if(resume == null || resume.isEmpty()){
            logger.error("Resume file is null or empty");
            throw new FailedProcessException("Resume file is required");
        }
        String resumeUrl = saveFile(resume);
        if(resumeUrl == null){
            logger.error("Failed to upload resume file");
            throw new FailedProcessException("Failed to upload resume file");
        }
        logger.info("MultipartFile name: {}", resume.getOriginalFilename());
        logger.info("MultipartFile size: {}", resume.getSize());
        String extractedText = extractTextFromResume(resume); // Implement resume parsing logic here
        logger.info("Extracted text from resume, length: {}", extractedText.length());
        CandidateModel candidateModel = createCandidateFromResume(extractedText, resumeUrl);
        logger.info("Created candidate from resume with ID: {}", candidateModel.getCandidateId());
        return mapper.toDto(candidateModel, CandidateResponseDto.class);
    }

    private CandidateModel createCandidateFromResume(String text, String resumeUrl) {
        String email = extractEmail(text);
        if(email == null){
            logger.error("Email not found in resume");
            throw new FailedProcessException("Email not found in resume");
        }
        if(modelService.existsUserByEmail(email)){
            logger.error("User with email {} already exists",email);
            throw new FailedProcessException("User with email "+email+" already exists");
        }
        UserModel newUser = new UserModel();
        newUser.setUserEmail(email);
        newUser.setUserName(email);
        String password = generatePassword();
        newUser.setUserPassword(passwordEncoder.encode(password));
        RoleModel candidateRole = modelService.getRole("CANDIDATE");
        newUser.setRole(candidateRole);
        UserModel savedUser = modelService.addUser(newUser);
        logger.info("Created new user for candidate with userId: {}", savedUser.getUserId());


        CandidateModel candidate = new CandidateModel();
        candidate.setCandidateAddress(extractAddress(text));
        logger.info("address extracted : {}",candidate.getCandidateAddress());
        candidate.setCandidateFirstName(extractName(text));
        logger.info("first name extracted : {}",candidate.getCandidateFirstName());
        candidate.setCandidateGender(extractGender(text));
        logger.info("gender : {}",candidate.getCandidateGender());
        candidate.setCandidatePhoneNumber(extractPhone(text));
        logger.info("phone number extracted : {}",candidate.getCandidatePhoneNumber());
        candidate.setCandidateTotalExperienceInYears(extractExperienceYears(text));
        logger.info("total experience extracted : {}",candidate.getCandidateTotalExperienceInYears());
        candidate.setCandidateZipCode(extractPinCode(text));
        logger.info("pinCode extracted : {}",candidate.getCandidateZipCode());
        candidate.setCandidateTotalExperienceInYears(extractExperienceYears(text));
        logger.info("total experience extracted : {}",candidate.getCandidateTotalExperienceInYears());
        candidate.setCandidateResumeUrl(resumeUrl);
        logger.info("Extracted basic candidate details from resume text");
        List<String> skills = extractSkills(text);
        if (!skills.isEmpty()) {
            skills.forEach(skill -> {
                SkillModel skillModel = null;
                if(modelService.existsSkill(skill)){
                    skillModel = modelService.getSkill(skill);
                }else{
                    SkillModel newSkillModel = new SkillModel();
                    newSkillModel.setSkill(skill);
                    skillModel = modelService.addSkill(newSkillModel);
                    logger.info("New skill '{}' added to the system", skill);
                }
                CandidateSkillModel newSkill = new CandidateSkillModel();
                newSkill.setCandidate(candidate);
                newSkill.setSkill(skillModel);
                newSkill.setProficiencyLevel(ProficiencyLevel.BEGINNER);
                newSkill.setYearsOfExperience(0);
                logger.info("Adding skill '{}' to candidate", skill);
                candidate.getCandidateSkills().add(newSkill);
            });
        }
        candidate.setUser(savedUser);
        logger.info("Saving candidate to database");
        emailService.mailToCredentialCandidate(email,email,password);
        return modelService.addCandidate(candidate);
//                candidateRepository.save(candidate);
    }

    public static String generatePassword() {
        logger.debug("Generating random password");

        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String DIGITS = "0123456789";
        String SPECIAL = "@#$%&*!";
        String ALL = UPPER + LOWER + DIGITS + SPECIAL;

        StringBuilder password = new StringBuilder(8);

        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < 8; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        char[] pwdArray = password.toString().toCharArray();
        for (int i = 0; i < pwdArray.length; i++) {
            int j = random.nextInt(pwdArray.length);
            char temp = pwdArray[i];
            pwdArray[i] = pwdArray[j];
            pwdArray[j] = temp;
        }

        String finalPassword = new String(pwdArray);

        logger.debug("Generated password (masked): ********");
        return finalPassword;
    }

    private String extractTextFromResume(MultipartFile resume) {
        try {
            Tika tika = new Tika();
            return tika.parseToString(resume.getInputStream());
        } catch (Exception e) {
            throw new FailedProcessException("Failed to read resume");
        }
    }

    private String extractEmail(String text) {
        Matcher m = Pattern.compile(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        ).matcher(text);
        return m.find() ? m.group() : null;
    }

    private String extractPhone(String text) {
        Matcher m = Pattern.compile(
                "(\\+91[-\\s]?)?[6-9]\\d{9}"
        ).matcher(text);
        if (m.find()) {
            return m.group().substring(4);
        }
        m = Pattern.compile(
                "\\b\\d{10}\\b"
        ).matcher(text);
        return m.find() ? m.group() : null;
    }

    private String extractName(String text) {
        String[] lines = text.split("\\R");
        for (int i = 0; i < Math.min(lines.length, 3); i++) {
            if (lines[i].length() < 40 && lines[i].matches("[A-Za-z ]+")) {
                return lines[i].trim();
            }
        }
        return null;
    }

    private List<String> extractSkills(String text) {
        List<SkillModel> skills = modelService.getAllSkills();
        return skills.stream()
                .map(SkillModel::getSkill)
                .filter(skill -> text.toLowerCase().contains(skill.toLowerCase()))
                .toList();
    }

    private Integer extractExperience(String text) {
        Matcher m = Pattern.compile("(\\d+)\\+?\\s+years?").matcher(text.toLowerCase());
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private String extractPinCode(String text) {
        Matcher m = Pattern.compile("\\b[1-9][0-9]{5}\\b").matcher(text);
        return m.find() ? m.group() : null;
    }

    private String extractGender(String text) {
        text = text.toLowerCase(Locale.ROOT);

        if (text.contains("male")) return "Male";
        if (text.contains("female")) return "Female";
        if (text.contains("other") || text.contains("non-binary")) return "Other";

        return null;
    }

    private String extractAddress(String text) {
        String[] lines = text.split("\\R");
        for (String line : lines) {
            if (line.toLowerCase().contains("address") ||
                    line.toLowerCase().contains("location")) {
                return line.replaceAll("(?i)address|location", "").trim();
            }
        }
        return null;
    }

    private Integer extractExperienceYears(String text) {
        Matcher m = Pattern.compile("(\\d+)\\+?\\s*(years?|yrs?)", Pattern.CASE_INSENSITIVE)
                .matcher(text);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private String saveFile(MultipartFile file) {
        try {
            logger.debug("Uploading file: {}", file.getOriginalFilename());
            String fileUrl = fileService.uploadImage(file);
            logger.debug("File uploaded successfully: {}", fileUrl);
            return fileUrl;
        }  catch (Exception e) {
            logger.error("Unexpected error while uploading file {}: {}", file.getOriginalFilename(), e.getMessage());
            return null;
        }
        catch (InvalidImageFormateException e) {
            throw new RuntimeException(e);
        }
    }

}
