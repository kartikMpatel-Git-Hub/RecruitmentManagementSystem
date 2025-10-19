package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config;

public class AppConstant {
    public static final Boolean TRUE_VALUE = true;
    public static final Boolean FALSE_VALUE = false;

    public static final Integer NORMAL = 1;
    public static final Integer ADMIN = 2;
    public static final Integer RECRUITER = 3;
    public static final Integer HR = 4;
    public static final Integer INTERVIEWER = 5;
    public static final Integer CANDIDATE = 6;

    public static Integer getRoleId(String role) {
        return switch (role) {
            case "NORMAL" -> NORMAL;
            case "ADMIN" -> ADMIN;
            case "RECRUITER" -> RECRUITER;
            case "HR" -> HR;
            case "INTERVIEWER" -> INTERVIEWER;
            case "CANDIDATE" -> CANDIDATE;
            default -> -1;
        };
    }
}
