package com.berd.dev.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.berd.dev.forms.CaisseMvtForm;
import com.berd.dev.services.CaisseMvtService;
import com.berd.dev.services.CaisseService;
import com.berd.dev.services.DepenseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/caisses-mvts")
@RequiredArgsConstructor
public class CaisseMvtController {

    private final CaisseMvtService caisseMvtService;
    private final CaisseService caisseService;



    private final DepenseService depenseService;

    @GetMapping("/liste")
    public String liste(Model model) {
        model.addAttribute("mvts", caisseMvtService.getAll());
        model.addAttribute("content", "pages/caisses/caisse-mvt-liste");
        return "admin-layout";
    }

    @GetMapping("/saisie")
    public String saisie(Model model, jakarta.servlet.http.HttpSession session) {
        model.addAttribute("caisses", caisseService.getAllDto());
        model.addAttribute("depenses", depenseService.getAllDto());
        model.addAttribute("lastMvts", caisseMvtService.getLastTransaction(5));

        CaisseMvtForm form = (CaisseMvtForm) session.getAttribute("caisseMvtForm");
        if (form != null) {
            model.addAttribute("caisseMvtForm", form);
        } else {
            model.addAttribute("caisseMvtForm", new CaisseMvtForm());
        }

        model.addAttribute("content", "pages/caisses/caisse-mvt-saisie");
        return "admin-layout";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CaisseMvtForm form, RedirectAttributes rd,
            HttpSession session) {
        try {
            caisseMvtService.saveByForm(form);
            session.removeAttribute("caisseMvtForm");
            rd.addFlashAttribute("toastMessage", "Mouvement enregistré avec succès");
            rd.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            session.setAttribute("caisseMvtForm", form);
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "warning");
            e.printStackTrace();
        } catch (Exception e) {
            session.setAttribute("caisseMvtForm", form);
            rd.addFlashAttribute("toastMessage", "Erreur lors de l'enregistrement: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
            e.printStackTrace();

        }
        return "redirect:/caisses-mvts/saisie";
    }

    @GetMapping("/annuler")
    public String annuler(jakarta.servlet.http.HttpSession session, RedirectAttributes rd) {
        session.removeAttribute("caisseMvtForm");
        rd.addFlashAttribute("toastMessage", "Opération annulée");
        rd.addFlashAttribute("toastType", "info");
        return "redirect:/caisses-mvts/saisie";
    }

}
