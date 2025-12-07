package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.other;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CandidateRowData {

    private int rowNumber;

    private String userName;
    private String userEmail;
    private String userPassword;
    private String roleName;

    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private Integer experience;

    private RowError error;

    public CandidateRowData(int rowNumber) {
        this.rowNumber = rowNumber;
        this.error = new RowError(rowNumber);
    }

    public boolean isValid() {
        return error.getFieldErrors().isEmpty();
    }
}