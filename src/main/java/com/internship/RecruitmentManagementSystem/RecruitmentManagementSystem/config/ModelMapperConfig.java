package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

//@Configuration
public class ModelMapperConfig {

//    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setAmbiguityIgnored(true).setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        mapper.addMappings(new PropertyMap<UserModel, UserResponseDto>() {
            @Override
            protected void configure() {
                map().getRole().setRoleId(source.getRole().getRoleId());
                map().getRole().setRole(source.getRole().getRole());
            }
        });

        return mapper;
    }
}
