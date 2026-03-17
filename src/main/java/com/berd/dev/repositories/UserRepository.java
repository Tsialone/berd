package com.berd.dev.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.berd.dev.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByValidationToken(String token);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
    List<User> findByUsernameContainingIgnoreCase(String username);

}
