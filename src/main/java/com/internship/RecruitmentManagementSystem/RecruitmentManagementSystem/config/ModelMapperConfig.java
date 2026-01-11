package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree.DegreeCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.register.RegisterUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.university.UniversityCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.NewUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateEducationResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateSkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.degree.DegreeResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewInterviewerResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview.InterviewerFeedbackResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.register.RegisterUserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.role.RoleResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundInterviewResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillRatingResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.university.UniversityResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true);
        // Candidate Mappings

        mapper.typeMap(CandidateModel.class, CandidateResponseDto.class)
                        .addMapping(
                                src -> src.getUser().getUserEmail(),
                                CandidateResponseDto::setUserEmail
                        ).addMapping(
                                src -> src.getUser().getUsername(),
                                CandidateResponseDto::setUserName
                        ).addMapping(
                                src -> src.getUser().getUserImageUrl(),
                                CandidateResponseDto::setUserImageUrl
                );

        // Candidate Skill
        mapper.typeMap(CandidateSkillModel.class, CandidateSkillResponseDto.class);
        mapper.typeMap(SkillCreateDto.class, SkillModel.class);

        // Candidate Education Mappings
         mapper.typeMap(CandidateEducationModel.class, CandidateEducationResponseDto.class);

         // University Mappings
        mapper.typeMap(UniversityModel.class, UniversityResponseDto.class);
        mapper.typeMap(UniversityCreateDto.class,UniversityModel.class);

        // Degree Mappings
        mapper.typeMap(DegreeModel.class, DegreeResponseDto.class);
        mapper.typeMap(DegreeCreateDto.class, DegreeModel.class);

        // Application Mappings

        mapper.typeMap(ApplicationModel.class, ApplicationResponseDto.class)
                .addMapping(
                        src -> src.getPosition().getPositionId(),
                        ApplicationResponseDto::setPositionId
                ).addMapping(
                        src -> src.getCandidate().getCandidateId(),
                        ApplicationResponseDto::setCandidateId
                );
        mapper.typeMap(ApplicationModel.class, ApplicationRoundResponseDto.class)
                .addMapping(
                        src -> src.getPosition().getPositionId(),
                        ApplicationRoundResponseDto::setPositionId
                ).addMapping(
                        src -> src.getCandidate().getCandidateId(),
                        ApplicationRoundResponseDto::setCandidateId
                );

        mapper.typeMap(ApplicationModel.class, ShortlistedApplicationResponseDto.class)
                .addMapping(
                        src -> src.getPosition().getPositionId(),
                        ShortlistedApplicationResponseDto::setPositionId
                ).addMapping(
                        src -> src.getCandidate().getCandidateId(),
                        ShortlistedApplicationResponseDto::setCandidateId
                );

        mapper.typeMap(ApplicationStatusModel.class, ApplicationStatusResponseDto.class);

        mapper.typeMap(ApplicationModel.class, MappedApplicationResponseDto.class)
                .addMapping(
                        src -> src.getPosition().getPositionId(),
                        MappedApplicationResponseDto::setPositionId
                ).addMapping(
                        src -> src.getCandidate().getCandidateId(),
                        MappedApplicationResponseDto::setCandidateId
                );

        // Role Mappings
        mapper.typeMap(RoleModel.class, RoleResponseDto.class);

        // Round Mappings

        mapper.typeMap(RoundModel.class, RoundInterviewResponseDto.class)
                        .addMapping(
                                src -> src.getApplication().getApplicationId(),
                                RoundInterviewResponseDto::setApplicationId
                        );


        mapper.typeMap(RoundModel.class, RoundResponseDto.class);

        // User Mappings
        mapper.typeMap(UserModel.class, UserResponseDto.class)
                        .addMapping(UserModel::getUsername,UserResponseDto::setUserName);
        mapper.typeMap(RegisterUserDto.class, UserModel.class);
        mapper.typeMap(RegisterUserDto.class, RegisterModel.class);
        mapper.typeMap(RegisterModel.class, UserModel.class);
        mapper.typeMap(RegisterModel.class, RegisterUserResponseDto.class)
                        .addMapping(
                                src -> src.getRole().getRole(),
                                RegisterUserResponseDto::setRole
                        );

        // Interview Mappings
        mapper.typeMap(InterviewModel.class, InterviewResponseDto.class)
                .addMapping(
                        src -> src.getRound().getApplication().getCandidate().getCandidateId(),
                        InterviewResponseDto::setCandidateId
                )
                .addMapping(
                        src -> src.getRound().getApplication().getPosition().getPositionId(),
                        InterviewResponseDto::setPositionId
                )
                .addMapping(
                        src -> src.getRound().getApplication().getApplicationId(),
                        InterviewResponseDto::setApplicationId
                );

        // Interviewer Mappings
        mapper.typeMap(InterviewInterviewerModel.class, InterviewInterviewerResponseDto.class);

        // Interviewer Feedback Mappings
        mapper.typeMap(InterviewerFeedbackModel.class, InterviewerFeedbackResponseDto.class);

        // Skill Rating Mappings
        mapper.typeMap(SkillRatingModel.class, SkillRatingResponseDto.class);
        return mapper;
    }
}
