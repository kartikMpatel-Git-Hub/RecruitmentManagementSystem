package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Stream;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Table(name = "tbl_degree")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DegreeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer degreeId;

    @Column(unique = true,length = 50)
    @NotEmpty(message = "Degree Can't Be Empty")
    private String degree;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stream stream;
}
