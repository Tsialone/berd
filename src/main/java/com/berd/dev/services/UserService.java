package com.berd.dev.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.berd.dev.models.User;
import com.berd.dev.repositories.ParamRepository;
import com.berd.dev.repositories.UserRepository;
import com.berd.dev.utils.DateUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ParamRepository paramRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    public User getByResetToken(String token) throws Exception {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Le token de réinitialisation ne peut pas être nul ou vide");
        }
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de réinitialisation invalide"));
        long duree_expiration = paramRepository.findByLibelle("expiration_token")
                .orElseThrow(() -> new IllegalStateException("Paramètre 'expiration_token' non trouvé"))
                .getValeurAsLong();
        long spendedMinutes = DateUtils.differenceEnMinutes(user.getCreatedResetToken(), LocalDateTime.now());
        if (spendedMinutes > duree_expiration) {
            throw new Exception("Token de réinitialisation expiré");
        }
        return user;
    }

    public User resetPassword(User user, String newPassword, String confirmPassword) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "L'utilisateur ne peut pas être nul");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être nul ou vide");
        }
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe de confirmation ne peut pas être nul ou vide");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetToken(null);
        user.setCreatedResetToken(null);
        return userRepository.save(user);
    }

    public User resetPasswordRequest(String userName, HttpServletRequest request) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé : " + userName));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Utilisateur non actif : " + userName);
        }
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setCreatedResetToken(LocalDateTime.now());
        sendEmailResetPassword(user, request);
        userRepository.save(user);


        return user;
    }

    public void sendEmailResetPassword(User user, HttpServletRequest request) {

        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");

        emailService.envoyerEmail(user.getEmail(), "Réinitialisation de votre mot de passe",
                "Veuillez cliquer sur le lien suivant pour réinitialiser votre mot de passe : " + baseUrl
                        + "/reset-password?token="
                        + user.getResetToken());

    }

    public List<User> getByLikeName(String username) {
        if (username == null || username.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findByUsernameContainingIgnoreCase(username.trim());
    }

    public User save(User user, HttpServletRequest request) {
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new IllegalArgumentException(
                    "L'utilisateur, le nom d'utilisateur, le mot de passe et l'email ne peuvent pas être nuls");
        }
        if (user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }
        if (user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        if (user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        User existingUser = userRepository.findByUsername(user.getUsername()).orElse(null);
        User existingUserEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (existingUserEmail != null && existingUserEmail.isActive()) {
            throw new IllegalArgumentException("L'email est déjà utilisé");
        }
        if (existingUser != null && existingUser.isActive()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est déjà pris");
        } else if (existingUser != null && !existingUser.isActive()) {
            user.setIdUtilisateur(existingUser.getIdUtilisateur());
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        String token = UUID.randomUUID().toString();
        user.setActive(false);
        user.setValidationToken(token);
        user.setCreatedToken(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        sendEmailVerification(savedUser, request);
        return savedUser;
    }

    public void activateUser(String token) throws Exception {
        User user = userRepository.findByValidationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de validation invalide"));
        long duree_expiration = paramRepository.findByLibelle("expiration_token")
                .orElseThrow(() -> new IllegalStateException("Paramètre 'expiration_token' non trouvé"))
                .getValeurAsLong();
        long spendedMinutes = DateUtils.differenceEnMinutes(user.getCreatedToken(), LocalDateTime.now());
        if (spendedMinutes > duree_expiration) {
            throw new Exception("Token de validation expiré");
        }
        user.setActive(true);
        user.setCreatedToken(null);
        user.setValidationToken(null);
        userRepository.save(user);
    }

    public void sendEmailVerification(User user, HttpServletRequest request) {

        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");

        emailService.envoyerEmail(user.getEmail(), "Activation de votre compte",
                "Veuillez cliquer sur le lien suivant pour valider votre compte : " + baseUrl + "/activate?token="
                        + user.getValidationToken());

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
    }
}
