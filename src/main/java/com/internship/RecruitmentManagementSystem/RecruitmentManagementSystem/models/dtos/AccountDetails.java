package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetails {
    private boolean userAccountNonExpired = true;
    private boolean userAccountNonLocked = true;
    private boolean userCredentialsNonExpired = true;
    private boolean userEnabled = true;

    @Override
    public String toString(){

        return "{" +
                "\n\tuserAccountNonExpired : " + userAccountNonExpired +
                "\n\tuserAccountNonLocked : " + userAccountNonLocked +
                "\n\tuserCredentialsNonExpired : " + userCredentialsNonExpired +
                "\n\tuserEnabled : " + userEnabled +
                "\n}";
    }
}

