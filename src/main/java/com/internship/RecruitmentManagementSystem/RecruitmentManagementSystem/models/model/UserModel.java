package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,length = 10)
    private Integer userId;

    @Column(unique = true,nullable = false,length = 30)
    @NotEmpty(message = "User Name Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String userName;

    @Column(nullable = false)
    @NotEmpty(message = "Password For User Can't Be Empty !")
    private String userPassword;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "tbl_user_roles",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "roleId")
    )
    private Set<RoleModel> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role ->new SimpleGrantedAuthority(role.getRole())).toList();
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return AppConstant.TRUE_VALUE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return AppConstant.TRUE_VALUE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return AppConstant.TRUE_VALUE;
    }

    @Override
    public boolean isEnabled() {
        return AppConstant.TRUE_VALUE;
    }
}
