package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config;

public class AppConstant {
    public static final Boolean TRUE_VALUE = true;
    public static final Boolean FALSE_VALUE = false;

    public static final Integer ROLE_NORMAL = 1;
    public static final Integer ROLE_ADMIN = 2;
    public static final Integer ROLE_RECRUITER = 3;
    public static final Integer ROLE_HR = 4;
    public static final Integer ROLE_INTERVIEWER = 5;
    public static final Integer ROLE_CANDIDATE = 6;

    public static Integer getRoleId(String role) {
        return switch (role) {
            case "ROLE_NORMAL" -> ROLE_NORMAL;
            case "ROLE_ADMIN" -> ROLE_ADMIN;
            case "ROLE_RECRUITER" -> ROLE_RECRUITER;
            case "ROLE_HR" -> ROLE_HR;
            case "ROLE_INTERVIEWER" -> ROLE_INTERVIEWER;
            case "ROLE_CANDIDATE" -> ROLE_CANDIDATE;
            default -> -1;
        };
    }
}
