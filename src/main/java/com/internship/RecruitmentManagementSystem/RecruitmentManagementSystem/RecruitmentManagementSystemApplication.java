package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableRetry
public class RecruitmentManagementSystemApplication {

	public static void main(String[] args) {
        SpringApplication.run(RecruitmentManagementSystemApplication.class, args);
	}

}
