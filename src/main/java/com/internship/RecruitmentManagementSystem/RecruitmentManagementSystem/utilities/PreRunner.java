package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PreRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public PreRunner(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            if (!roleRepository.existsByRole("ROLE_USER")) {
                RoleModel simpleUser = new RoleModel();
                simpleUser.setRoleId(AppConstant.NORMAL_ROLE); // only if NOT auto-generated
                simpleUser.setRole("ROLE_USER");
                roleRepository.save(simpleUser);
            }

            if (!roleRepository.existsByRole("ROLE_ADMIN")) {
                RoleModel adminUser = new RoleModel();
                adminUser.setRoleId(AppConstant.ADMIN_ROLE); // only if NOT auto-generated
                adminUser.setRole("ROLE_ADMIN");
                roleRepository.save(adminUser);
            }
            System.out.println("Roles added successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Application Started !");
    }
}
