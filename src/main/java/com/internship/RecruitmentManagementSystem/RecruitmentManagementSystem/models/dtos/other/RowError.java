package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RowError {

    private int rowNumber;
    private Map<String, String> fieldErrors = new HashMap<>();

    public RowError(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public void addFieldError(String field, String error) {
        fieldErrors.put(field, error);
    }

}
