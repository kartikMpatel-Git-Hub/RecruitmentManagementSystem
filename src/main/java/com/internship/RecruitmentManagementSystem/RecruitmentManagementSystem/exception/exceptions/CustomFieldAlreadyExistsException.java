package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions;

import lombok.Getter;

@Getter
public class CustomFieldAlreadyExistsException extends RuntimeException {
    private final String field;

    public CustomFieldAlreadyExistsException(String field, String message) {
        super(message);
        this.field = field;
    }

}
