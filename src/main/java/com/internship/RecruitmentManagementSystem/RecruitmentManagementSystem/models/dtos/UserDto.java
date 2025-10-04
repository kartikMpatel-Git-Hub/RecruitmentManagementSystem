package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Integer userId;

    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPassword;

    private String userEmail;

    private String userImageUrl;

    private Set<RoleModel> roles = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean userEnabled = true;
}
