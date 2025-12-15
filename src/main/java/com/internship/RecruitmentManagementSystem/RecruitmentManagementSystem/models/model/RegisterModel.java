package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity(name = "tbl_register")
@RequiredArgsConstructor
@Getter
@Setter
public class RegisterModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,length = 10)
    private Integer registerId;

    @Column(unique = true,nullable = false,length = 30)
    @NotEmpty(message = "User Name Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String userName;

    @Column(nullable = false)
    @NotEmpty(message = "Password For User Can't Be Empty !")
    private String userPassword;

    @Column(unique = true,nullable = false)
    @NotEmpty(message = "Email Can't Be Empty !")
    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Invalid email format!"
    )
    private String userEmail;

    @Column(length = 500)
    private String userImageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id",nullable = false)
    private RoleModel role;

}
