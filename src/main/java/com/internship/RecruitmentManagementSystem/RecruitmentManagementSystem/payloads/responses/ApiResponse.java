package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private Integer statusCode = 404;
    private String message = "something Went Wrong !";
    private Boolean success = false;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status code = '" + statusCode + '\'' +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
