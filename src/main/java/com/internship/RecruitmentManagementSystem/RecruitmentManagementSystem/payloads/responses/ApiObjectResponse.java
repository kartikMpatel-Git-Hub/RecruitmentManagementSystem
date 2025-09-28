package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiObjectResponse {
    private Integer statusCode = 404;
    private Object data;
    private Boolean success = false;
}
