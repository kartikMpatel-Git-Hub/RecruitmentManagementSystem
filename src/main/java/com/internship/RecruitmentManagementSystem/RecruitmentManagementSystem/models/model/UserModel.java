package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_user",indexes = {
        @Index(name = "idx_user_name", columnList = "user_name"),
        @Index(name = "idx_user_email", columnList = "user_email"),
        @Index(name = "idx_role_id", columnList = "role_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserModel extends BaseEntity implements UserDetails {

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

    @Column(name = "user_account_non_expired", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean userAccountNonExpired = true;

    @Column(name = "user_account_non_locked", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean userAccountNonLocked = true;

    @Column(name = "user_credentials_non_expired", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean userCredentialsNonExpired = true;

    @Column(name = "user_enabled", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean userEnabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
        return authorities;
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
        return this.userAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.userAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() { return this.userCredentialsNonExpired; }

    @Override
    public boolean isEnabled() {
        return this.userEnabled;
    }
}
