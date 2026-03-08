package com.berd.dev.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.berd.dev.services.UniteService;

import jakarta.servlet.http.HttpSession;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UniteService uniteService;

    @GetMapping("/")
    public String getListe(Model model) {

        uniteService.getAll();
        model.addAttribute("content", "pages/home");
        return "admin-layout";
    }

    @GetMapping("/login")
    public String login(Model model) {

        return "pages/auths/login-saisie";
    }

    @GetMapping("/signin")
    public String sign(Model model) {

        return "pages/auths/signin-saisie";
    }

    @GetMapping("/logout")
    public String logout(Model model , HttpSession session) {
        session.invalidate();
        return "pages/auths/login-saisie";
    }

}
