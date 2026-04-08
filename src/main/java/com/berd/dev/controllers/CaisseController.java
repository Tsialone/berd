package com.berd.dev.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.berd.dev.dtos.CaisseDto;
import com.berd.dev.dtos.CaisseCategorieDto;
import com.berd.dev.forms.CaisseForm;
import com.berd.dev.mappers.CaisseCategorieMapper;
import com.berd.dev.models.Caisse;
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
    public String liste(
            @RequestParam(required = false) Integer categorieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String search,
            Model model) {
        
        var caisses = caisseService.filterCaisses(categorieId, dateDebut, dateFin, search);
        var categories = caisseCategoreRepository.findAll();
        
        // Calculer le solde total
        Double soldeTotal = caisses.stream()
                .mapToDouble(c -> c.getSolde() != null ? c.getSolde() : 0d)
                .sum();
        
        model.addAttribute("caisses", caisses);
        model.addAttribute("categories", categories);
        model.addAttribute("soldeTotal", soldeTotal);
        model.addAttribute("content", "pages/caisses/caisse-liste");
        return "admin-layout";
    }

    @GetMapping("/fiche/{id}")
    public String fiche(@PathVariable Integer id, Model model) {
        var caisse = caisseService.getCaisseWithMvt(id);
        model.addAttribute("caisse", caisse);
        model.addAttribute("mouvements", caisse.getCaisseMvts().stream ().sorted((m1, m2) -> m2.getCreated().compareTo(m1.getCreated())));
        model.addAttribute("content", "pages/caisses/caisse-fiche");
        return "admin-layout";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, RedirectAttributes rd) {
        try {
            Caisse caisse = caisseService.getCaisseById(id);
            List<CaisseCategorieDto> categories = CaisseCategorieMapper.tDtos(caisseCategoreRepository.findAll());
            
            model.addAttribute("caisse", caisse);
            model.addAttribute("categories", categories);
            model.addAttribute("content", "pages/caisses/caisse-edit");
            return "admin-layout";
        } catch (IllegalArgumentException e) {
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "warning");
            return "redirect:/caisses/liste";
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Erreur lors de la récupération de la caisse: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/caisses/liste";
        }
    }

    @GetMapping("/saisie")
    public String saisie(Model model, jakarta.servlet.http.HttpSession session) {
        model.addAttribute("categories", caisseCategoreRepository.findAll());
        model.addAttribute("lastCaisses", caisseService.getLastTransaction(10));

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
            e.printStackTrace();
            session.setAttribute("caisseForm", form);
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "warning");
        } catch (Exception e) {
            session.setAttribute("caisseForm", form);
            rd.addFlashAttribute("toastMessage", "Erreur lors de l'enregistrement: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
            e.printStackTrace();
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

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes rd) {
        try {
            caisseService.delete(id);
            rd.addFlashAttribute("toastMessage", "Caisse supprimée avec succès");
            rd.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "warning");
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Erreur lors de la suppression: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
            e.printStackTrace();
        }
        return "redirect:/caisses/liste";
    }

}
