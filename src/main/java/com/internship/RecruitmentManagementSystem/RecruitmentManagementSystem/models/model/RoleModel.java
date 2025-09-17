package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleModel {

    @Id
    @Column(nullable = false,length = 10)
    private Integer roleId;

    @Column(unique = true,length = 15)
    @NotEmpty(message = "Role Can't Be Empty !")
    @Size(min = 1,max = 15)
    private String role;

}
