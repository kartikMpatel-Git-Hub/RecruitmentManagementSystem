package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomBean {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
