package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other.CandidateRowData;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class BulkCandidateService {

    private static final Logger logger = LoggerFactory.getLogger(BulkCandidateService.class);

    private final ExcelValidationService validationService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelServiceInterface modelService;

    private static final SecureRandom random = new SecureRandom();

    public void processSingleRow(Row row) {

        int rowNum = row.getRowNum() + 1;
        logger.info("Processing row number: {}", rowNum);

        CandidateRowData data = extractRow(row, rowNum);
        logger.debug("Extracted data for row {}: {}", rowNum, data);

        logger.debug("Validating user fields for row {}", rowNum);
        validationService.validateUser(data);

        logger.debug("Validating candidate fields for row {}", rowNum);
        validationService.validateCandidate(data);

        if (!data.isValid()) {
            String errorMsg = String.join(", ", data.getError().getFieldErrors().values());
            logger.error("Row {} validation failed: {}", rowNum, errorMsg);
            throw new RuntimeException(errorMsg);
        }

        logger.debug("Fetching role: {}", data.getRoleName());
        RoleModel role = modelService.getRole(data.getRoleName());

        logger.debug("Mapping row {} to UserModel", rowNum);
        UserModel user = mapToUser(data, role);

        data.setUserPassword(user.getUserPassword());
        logger.debug("Encoding password for user: {}", data.getUserName());
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        modelService.addUser(user);

        logger.info("User created successfully for row {} with username: {}", rowNum, user.getUsername());

        logger.debug("Mapping row {} to CandidateModel", rowNum);
        CandidateModel candidate = mapToCandidate(data, user);
        modelService.addCandidate(candidate);

        logger.info("Candidate saved successfully for row {} ({})", rowNum, data.getFirstName());

        logger.info("Sending credential mail to candidate: {}", data.getUserEmail());
        emailService.mailToCandidate(data);
    }

    private CandidateRowData extractRow(Row row, int rowNum) {
        logger.debug("Extracting Excel row {}", rowNum);

        CandidateRowData data = new CandidateRowData(rowNum);
        data.setRoleName("CANDIDATE");
        data.setUserName(ExcelUtils.getString(row, 0));
        data.setUserEmail(ExcelUtils.getString(row, 1));
        data.setFirstName(ExcelUtils.getString(row, 2));
        data.setMiddleName(ExcelUtils.getString(row, 3));
        data.setLastName(ExcelUtils.getString(row, 4));
        data.setPhone(ExcelUtils.getString(row, 5));
        data.setGender(ExcelUtils.getString(row, 6));
        data.setDob(ExcelUtils.getDate(row, 7));
        data.setAddress(ExcelUtils.getString(row, 8));
        data.setCity(ExcelUtils.getString(row, 9));
        data.setState(ExcelUtils.getString(row, 10));
        data.setCountry(ExcelUtils.getString(row, 11));
        data.setZip(ExcelUtils.getString(row, 12));
        data.setExperience(ExcelUtils.getInteger(row, 13));

        logger.debug("Row {} extraction complete: {}", rowNum, data);

        return data;
    }

    private UserModel mapToUser(CandidateRowData d, RoleModel role) {
        logger.debug("Mapping user for username: {}", d.getUserName());

        UserModel user = new UserModel();
        user.setUserName(d.getUserName());
        user.setUserEmail(d.getUserEmail());
        user.setUserPassword(generatePassword());
        user.setRole(role);
        user.setUserAccountNonExpired(true);
        user.setUserAccountNonLocked(true);
        user.setUserCredentialsNonExpired(true);
        user.setUserEnabled(true);

        return user;
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

    private CandidateModel mapToCandidate(CandidateRowData d, UserModel user) {
        logger.debug("Mapping candidate for user: {}", user.getUsername());

        CandidateModel c = new CandidateModel();
        c.setCandidateFirstName(d.getFirstName());
        c.setCandidateMiddleName(d.getMiddleName());
        c.setCandidateLastName(d.getLastName());
        c.setCandidatePhoneNumber(d.getPhone());
        c.setCandidateGender(d.getGender());
        c.setCandidateDateOfBirth(d.getDob());
        c.setCandidateAddress(d.getAddress());
        c.setCandidateCity(d.getCity());
        c.setCandidateState(d.getState());
        c.setCandidateCountry(d.getCountry());
        c.setCandidateZipCode(d.getZip());
        c.setCandidateTotalExperienceInYears(d.getExperience());
        c.setUser(user);

        return c;
    }
}
