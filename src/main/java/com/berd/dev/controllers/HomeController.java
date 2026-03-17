package com.berd.dev.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.berd.dev.models.User;
import com.berd.dev.services.UniteService;
import com.berd.dev.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UniteService uniteService;

    private final UserService userService;

    @GetMapping("/activate")
    public String activate(@RequestParam("token") String token, Model model, RedirectAttributes rd,
            HttpServletRequest request) {

        try {
            userService.activateUser(token);

        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/signin";

        }

        rd.addFlashAttribute("toastMessage", "Votre compte a été activé avec succès.");
        rd.addFlashAttribute("toastType", "success");
        request.getSession().removeAttribute("user");
        return "redirect:/";

    }

    @GetMapping("/home")
    public String getListe(Model model) {

        uniteService.getAll();
        model.addAttribute("content", "pages/home");
        return "admin-layout";
    }

    @GetMapping("/forgot")
    public String forgot(HttpServletRequest request, Model model) {

        return "pages/auths/forgot-saisie";
    }

    @GetMapping("/")
    public String login(HttpServletRequest request, Model model) {
        // On récupère le message précis qu'on a mis juste au-dessus
        uniteService.getAll();
        String error = (String) request.getSession().getAttribute("loginErrorMessage");
        String username = (String) request.getSession().getAttribute("username");
        String password = (String) request.getSession().getAttribute("password");

        String logout = request.getParameter("logout");

        if (error != null) {
            model.addAttribute("toastMessage", error);
            model.addAttribute("toastType", "error");
            // IMPORTANT : On le supprime pour ne pas qu'il reste au prochain refresh (F5)

            model.addAttribute("username", username);
            model.addAttribute("password", password);
            request.getSession().removeAttribute("loginErrorMessage");
        }
        if (logout != null) {
            model.addAttribute("toastMessage", "Vous êtes déconnecté.");
            model.addAttribute("toastType", "info");
        }
        return "pages/auths/login-saisie";
    }

    @PostMapping("/login")
    public String login(User user) {

        return "pages/auths/login-saisie";
    }

    @GetMapping("/signin")
    public String sign(Model model, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        model.addAttribute("user", user);

        return "pages/auths/signin-saisie";
    }

    @PostMapping("/signin")
    public String sign(User user, RedirectAttributes rd, HttpServletRequest request) {

        try {
            System.out.println(user);
            String currPassword = new String(user.getPassword());
            userService.save(user, request);
            user.setPassword(currPassword);
        } catch (Exception e) {
            e.printStackTrace();
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/signin";
        }
        rd.addFlashAttribute("toastMessage",
                "Inscription réussie! Veuillez vérifier votre email pour activer votre compte.");
        rd.addFlashAttribute("toastType", "success");

        System.out.println(user);

        rd.addFlashAttribute("user", user);
        request.getSession().setAttribute("user", user);

        return "redirect:/signin";
    }

    // @GetMapping("/logout")
    // public String logout(Model model, HttpSession session) {
    // session.invalidate();
    // return "pages/auths/login-saisie";
    // }

}
