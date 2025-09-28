package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Integer statusCode = 400;
    private String message = "something Went Wrong !";
    private List<String> errors = new ArrayList<>();
    private Boolean success = false;
}
