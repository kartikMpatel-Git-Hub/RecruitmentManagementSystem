package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.degree;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DegreeUpdateDto {

    private String degree;

    private Stream stream;

}
