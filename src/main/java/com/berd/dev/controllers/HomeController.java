package com.berd.dev.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berd.dev.models.Unite;
import com.berd.dev.services.UniteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UniteService uniteService;
    @GetMapping ("/")
    public String getListe(Model model) {


        
        List  <Unite> unites = uniteService.getAll();
        System.out.println("refresh: " + unites.size());
        model.addAttribute("content", "pages/home");
        return "admin-layout";
    }

}
