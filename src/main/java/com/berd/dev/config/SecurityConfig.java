package com.berd.dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/activate", "/forgot", "/signin", "/public_api/**", "/assets/**" , "/forgot-reset" , "/reset-password").permitAll()
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Reservé Admin
                        .anyRequest().authenticated() // Tout le reste protégé
                )
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login") // L'URL que le formulaire doit "POSTER"
                        .usernameParameter("username") // Le 'name' de l'input HTML pour l'utilisateur
                        .passwordParameter("password") // Le 'name' de l'input HTML pour le mot de passe
                        .defaultSuccessUrl("/home", true)
                        .failureHandler((request, response, exception) -> {
                            String errorMessage = "Identifiants invalides"; // Message par défaut
                            String username = request.getParameter("username");
                            String password = request.getParameter("password");

                            // On teste l'exception précise balancée par Spring
                            if (exception instanceof BadCredentialsException) {
                                errorMessage = "Mot de passe incorrect ou utilisateur inconnu.";
                            } else if (exception instanceof DisabledException) {
                                errorMessage = "Votre compte n'est pas encore activé.";
                            } else if (exception instanceof LockedException) {
                                errorMessage = "Votre compte est verrouillé (trop d'essais).";
                            } else if (exception instanceof CredentialsExpiredException) {
                                errorMessage = "Votre compte n'est pas encore activé. Veuillez renvoyer la demande d'activation.";
                            }

                            System.out.println(exception.getClass().getName());

                            // On met le message en session (Flash) pour le récupérer au GET "/"
                            request.getSession().setAttribute("loginErrorMessage", errorMessage);
                            request.getSession().setAttribute("username", username);
                            request.getSession().setAttribute("password", password);

                            response.sendRedirect("/?error");
                        }).permitAll())
                .rememberMe(remember -> remember.key("maCleSecreteUniqueEtLongue") // Clé pour chiffrer le cookie
                        .tokenValiditySeconds(86400 * 14) // Durée : 14 jours (en secondes)
                        .rememberMeParameter("remember-me") // Le 'name' de la checkbox HTML
                ).logout(logout -> logout.logoutUrl("/logout") // L'URL que le formulaire POST utilise
                        .logoutSuccessUrl("/?logout=true") // Redirection vers le login avec un message
                        .invalidateHttpSession(true) // Détruit la session
                        .deleteCookies("JSESSIONID") // Nettoie les cookies
                        .permitAll());
        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // return new BCryptPasswordEncoder(); // Hachage sécurisé des mots de passe
          return NoOpPasswordEncoder.getInstance();
    }
}
