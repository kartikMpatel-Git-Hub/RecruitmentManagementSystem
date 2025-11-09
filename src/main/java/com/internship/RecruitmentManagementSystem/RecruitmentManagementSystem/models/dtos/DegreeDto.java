package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class DegreeDto {

    private Integer degreeId;

    private String degree;

    private Stream stream;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
