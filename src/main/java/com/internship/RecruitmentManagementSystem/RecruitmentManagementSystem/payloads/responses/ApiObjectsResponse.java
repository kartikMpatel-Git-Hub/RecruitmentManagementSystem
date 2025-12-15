package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiObjectsResponse {

    private Integer statusCode = 404;
    private List<Object> data;
    private Boolean success = false;

}
