package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

//@SpringBootApplication
public class PreRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public PreRunner(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            String[] roles = {
                    "NORMAL","ADMIN","RECRUITER","HR","INTERVIEWER","CANDIDATE"
            };
            for (String role : roles) {
                if(!roleRepository.existsByRole(role)){
                    RoleModel roleModel = new RoleModel();
                    roleModel.setRoleId(AppConstant.getRoleId(role));
                    roleModel.setRole(role);
                    roleRepository.save(roleModel);
                }
            }
            System.out.println("Roles added successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Application Started !");
    }
}
