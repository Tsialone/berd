package com.berd.dev.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.berd.dev.utils.jackson.EmailMaskingSerializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity(name = "utilisateur")
@Data
public class User implements UserDetails {
    @Id
    @Column(name = "id_utilisateur")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUtilisateur;

    @Column(unique = true, name = "nom", nullable = false)
    private String username;

    @Column(name = "mdp", nullable = false)
    private String password;

    private String role = "ROLE_USER";

    private boolean active = false;

    @Column(name = "validation_token")
    private String validationToken;

    @Column(name = "created_token")
    private LocalDateTime createdToken;

    @Column(name = "created_reset_token")
    private LocalDateTime createdResetToken;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(unique = true, name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "utilisateur")
    private List<Depense> depenses;

    @OneToMany(mappedBy = "utilisateur" , fetch =  FetchType.LAZY)
    private List<CategorieDepense> categorieDepenses;

    @Transient
    @JsonProperty("maskedEmail")
    @JsonSerialize(using = EmailMaskingSerializer.class)
    public String getMaskedEmail() {
        return this.email;
    }

    @JsonIgnore
    public String getEmail() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.active;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
