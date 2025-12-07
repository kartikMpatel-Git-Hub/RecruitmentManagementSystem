package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other.CandidateRowData;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.ExcelUtils;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.HtmlTemplateBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class BulkCandidateService {

    private final ExcelValidationService validationService;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final HtmlTemplateBuilder  htmlTemplateBuilder;
    private final EmailService emailService;
    private static final SecureRandom random = new SecureRandom();

    public void processSingleRow(Row row) {
        int rowNum = row.getRowNum() + 1;
        CandidateRowData data = extractRow(row, rowNum);

        validationService.validateUser(data);
        validationService.validateCandidate(data);

        if (!data.isValid()) {
            throw new RuntimeException(String.join(", ", data.getError().getFieldErrors().values()));
        }

        RoleModel role = roleRepository.findByRole(data.getRoleName())
                .orElseThrow(() -> new RuntimeException("Invalid role"));

        UserModel user = mapToUser(data, role);
        data.setUserPassword(user.getUserPassword());
        user.setUserPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        CandidateModel candidate = mapToCandidate(data, user);
        candidateRepository.save(candidate);

        mailToCandidate(data);
    }

    private void mailToCandidate(CandidateRowData data) {
        String mailBody = htmlTemplateBuilder.buildCandidateCredentialTemplate(
                data.getFirstName() + " " + data.getLastName(),
                data.getUserName(),
                data.getUserPassword()
        );

        emailService.sendMail(
                "kartikpatel7892@gmail.com",
                data.getUserEmail(),
                "Account Credentials - Recruitment Management System",
                mailBody
        );
    }

    private CandidateRowData extractRow(Row row, int rowNum) {
        CandidateRowData data = new CandidateRowData(rowNum);
        data.setUserName(ExcelUtils.getString(row, 0));
        data.setUserEmail(ExcelUtils.getString(row, 1));
        data.setRoleName(ExcelUtils.getString(row, 2));

        data.setFirstName(ExcelUtils.getString(row, 3));
        data.setMiddleName(ExcelUtils.getString(row, 4));
        data.setLastName(ExcelUtils.getString(row, 5));
        data.setPhone(ExcelUtils.getString(row, 6));
        data.setGender(ExcelUtils.getString(row, 7));
        data.setDob(ExcelUtils.getDate(row, 8));
        data.setAddress(ExcelUtils.getString(row, 9));
        data.setCity(ExcelUtils.getString(row, 10));
        data.setState(ExcelUtils.getString(row, 11));
        data.setCountry(ExcelUtils.getString(row, 12));
        data.setZip(ExcelUtils.getString(row, 13));
        data.setExperience(ExcelUtils.getInteger(row, 14));
        return data;
    }

    private UserModel mapToUser(CandidateRowData d, RoleModel role) {
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

        return new String(pwdArray);
    }

    private CandidateModel mapToCandidate(CandidateRowData d, UserModel user) {
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

