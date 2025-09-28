package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DegreeDto {

    private Integer degreeId;

    private String degree;

    private Stream stream;

}
