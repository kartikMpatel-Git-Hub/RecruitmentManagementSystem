package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.UserRole;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
@Data
public class PreRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PreRunner.class);
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            String[] roles = {
                    "NORMAL","ADMIN","RECRUITER","HR","INTERVIEWER","CANDIDATE","REVIEWER"
            };
            UserRole [] userRoles = UserRole.values();
            for(UserRole role : userRoles){
//            for (String role : roles) {
                if(!roleRepository.existsByRole(role.toString())){
                    RoleModel roleModel = new RoleModel();
                    roleModel.setRoleId(AppConstant.getRoleId(role.toString()));
                    roleModel.setRole(role.toString());
                    RoleModel savedRole = roleRepository.save(roleModel);
                    if(role.equals(UserRole.ADMIN)){
                        UserModel adminUser = new UserModel();
                        adminUser.setUserName("superAdmin");
                        adminUser.setUserEmail("superAdmin@gmail.com");
                        adminUser.setUserPassword(passwordEncoder.encode("Super@dmin.018"));
                        adminUser.setRole(savedRole);

                        userRepository.save(adminUser);
                    }
                }else{
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Recruitment Management System Live On : http://localhost:8080");
    }
}
