package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_role",indexes = {
        @Index(name = "idx_role", columnList = "role"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RoleModel extends BaseEntity{

    @Id
    @Column(nullable = false,length = 10)
    private Integer roleId;

    @Column(unique = true,length = 30)
    @NotEmpty(message = "Role Can't Be Empty !")
    @Size(min = 1,max = 30)
    private String role;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserModel> users = new HashSet<>();

}
