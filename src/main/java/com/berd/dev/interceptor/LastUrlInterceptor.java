package com.berd.dev.interceptor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LastUrlInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // On enregistre si : c'est un GET, l'utilisateur est connecté, et ce n'est pas une page d'erreur
        if ("GET".equalsIgnoreCase(request.getMethod()) && 
            auth != null && auth.isAuthenticated() && 
            !(auth instanceof AnonymousAuthenticationToken) &&
            response.getStatus() == 200) {
            
            String uri = request.getRequestURI();
            // On n'enregistre PAS la racine (le login)
            if (!uri.equals("/")) {
                Cookie cookie = new Cookie("LAST_URL", uri);
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours de survie dans Chrome
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
    }
}

