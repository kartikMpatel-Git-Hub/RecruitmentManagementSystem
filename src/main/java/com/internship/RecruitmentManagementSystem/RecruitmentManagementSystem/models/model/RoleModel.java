package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @Column(unique = true,length = 30)
    @NotEmpty(message = "Role Can't Be Empty !")
    @Size(min = 1,max = 30)
    private String role;

}
