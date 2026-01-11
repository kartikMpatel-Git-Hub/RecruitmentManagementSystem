package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RegisterException extends RuntimeException {
    private List<String> errors;

    public RegisterException(List<String> errors) {
        this.errors = new ArrayList<>();
        if (errors != null) {
            System.out.println("errors : " + errors.size());
            this.errors.addAll(errors);
        }
    }
}
