package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseException extends RuntimeException {
    ErrorResponse error;
    public ErrorResponseException(String message,ErrorResponse error) {
        super(message);
        this.error = error;
    }
}
