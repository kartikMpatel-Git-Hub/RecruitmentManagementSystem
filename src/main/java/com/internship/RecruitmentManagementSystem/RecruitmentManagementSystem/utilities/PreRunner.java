package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Stream;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.UserRole;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.ModelService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;

@SpringBootApplication
@Data
public class PreRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PreRunner.class);
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelService modelService;

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
                        adminUser.setUserImageUrl("https://res.cloudinary.com/dcpvyecl2/image/upload/v1768161989/oss/image/murrxkvkn0k5h7xkulmt.png");
                        adminUser.setUserPassword(passwordEncoder.encode("Super@dmin.018"));
                        adminUser.setRole(savedRole);
                        userRepository.save(adminUser);
                        addData();
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

    @Transactional
    private void addData() {
        addUniversities();
        addDegrees();
        addSkills();
    }

    private void addSkills() {
        String skills[] = {
                "Java","Spring Boot","Hibernate","SQL","NoSQL","JavaScript",
                "React","Angular","Node.js","Python","Django","Flask",
                "C++","C#","Ruby on Rails","PHP","HTML/CSS","AWS",
                "Azure","Docker","Kubernetes","Git","Machine Learning",
                "Data Analysis","DevOps"
        };
        Arrays.stream(skills).forEach(skillName -> {
            SkillModel skill = new SkillModel();
            skill.setSkill(skillName);
            modelService.addSkill(skill);
        });
    }

    private void addDegrees() {
        HashMap<String,String> degrees = new HashMap<>();
        degrees.put("Bachelor of Technology","science");
        degrees.put("Master of Technology","science");
        degrees.put("Bachelor of Science","science");
        degrees.put("Master of Science","science");
        degrees.put("Bachelor of Computer Applications","commerce");
        degrees.put("Master of Computer Applications","commerce");
        degrees.put("Bachelor of Business Administration","commerce");
        degrees.put("Master of Business Administration","commerce");
        degrees.put("Doctor of Philosophy","science");
        degrees.put("Bachelor of Arts","arts");
        degrees.put("Master of Arts","arts");
        degrees.forEach((degreeName,degreeField)->{
            DegreeModel degree = new DegreeModel();
            degree.setDegree(degreeName);
            degree.setStream(Stream.valueOf(degreeField));
            modelService.addDegree(degree);
        });
    }

    private void addUniversities() {
        String universities[] = {
                "Harvard University",
                "Stanford University",
                "Massachusetts Institute of Technology (MIT)",
                "University of California, Berkeley",
                "California Institute of Technology (Caltech)",
                "University of Oxford",
                "University of Cambridge",
                "Princeton University",
                "Yale University",
                "Columbia University",
                "University of Chicago",
                "University of Pennsylvania",
                "Cornell University",
                "University of Michigan",
                "Duke University"
        };
        Arrays.stream(universities).forEach(universityName -> {
            UniversityModel university = new UniversityModel();
            university.setUniversity(universityName);
            modelService.addUniversity(university);
        });
    }
}
