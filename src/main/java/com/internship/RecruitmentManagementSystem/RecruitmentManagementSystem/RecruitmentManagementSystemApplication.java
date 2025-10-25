package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RecruitmentManagementSystemApplication {

	public static void main(String[] args) {

        SpringApplication.run(RecruitmentManagementSystemApplication.class, args);
	}

}
