package com.berd.dev.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.berd.dev.forms.CaisseForm;
import com.berd.dev.services.CaisseService;
import com.berd.dev.repositories.CaisseCategoreRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/caisses")
@RequiredArgsConstructor
public class CaisseController {

    private final CaisseService caisseService;
    private final CaisseCategoreRepository caisseCategoreRepository;

    @GetMapping("/liste")
    public String liste(Model model) {
        model.addAttribute("caisses", caisseService.getAll());
        model.addAttribute("content", "pages/caisses/caisse-liste");
        return "admin-layout";
    }

    @GetMapping("/saisie")
    public String saisie(Model model, jakarta.servlet.http.HttpSession session) {
        model.addAttribute("categories", caisseCategoreRepository.findAll());

        CaisseForm form = (CaisseForm) session.getAttribute("caisseForm");
        if (form != null) {
            model.addAttribute("caisseForm", form);
        } else {
            model.addAttribute("caisseForm", new CaisseForm());
        }

        model.addAttribute("content", "pages/caisses/caisse-saisie");
        return "admin-layout";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CaisseForm form, RedirectAttributes rd,
            jakarta.servlet.http.HttpSession session) {
        try {

            caisseService.saveByForm(form);
            session.removeAttribute("caisseForm");
            rd.addFlashAttribute("toastMessage", "Caisse enregistrée avec succès");
            rd.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            session.setAttribute("caisseForm", form);
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "warning");
        } catch (Exception e) {
            session.setAttribute("caisseForm", form);
            rd.addFlashAttribute("toastMessage", "Erreur lors de l'enregistrement: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
        }
        return "redirect:/caisses/saisie";
    }

    @GetMapping("/annuler")
    public String annuler(jakarta.servlet.http.HttpSession session, RedirectAttributes rd) {
        session.removeAttribute("caisseForm");
        rd.addFlashAttribute("toastMessage", "Opération annulée");
        rd.addFlashAttribute("toastType", "info");
        return "redirect:/caisses/liste";
    }

}
