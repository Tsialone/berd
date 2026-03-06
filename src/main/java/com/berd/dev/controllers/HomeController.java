package com.berd.dev.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berd.dev.services.UniteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UniteService  uniteService;

    @GetMapping ("/")
    public String getListe(Model model) {

        uniteService.getAll();
        model.addAttribute("content", "pages/home");
        return "admin-layout";
    }

}
