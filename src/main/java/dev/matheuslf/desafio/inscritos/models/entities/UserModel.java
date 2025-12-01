package dev.matheuslf.desafio.inscritos.models.entities;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "TB_USERS")
public class UserModel implements UserDetails {
    private static final long serialVersion = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    public UserModel(String fullName, String email, String password, UserRole role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_PM"),
                    new SimpleGrantedAuthority("ROLE_DEV"),
                    new SimpleGrantedAuthority("ROLE_VIEWER"));
        } else if (this.role == UserRole.PM) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_PM"),
                    new SimpleGrantedAuthority("ROLE_DEV"),
                    new SimpleGrantedAuthority("ROLE_VIEWER"));
        } else if (this.role == UserRole.DEV) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_DEV"),
                    new SimpleGrantedAuthority("ROLE_VIEWER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_VIEWER"));
        }
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
