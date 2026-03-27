package com.berd.dev.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.berd.dev.models.CategorieDepense;
import com.berd.dev.models.CategorieDepenseDetail;
import com.berd.dev.services.CategorieDepenseDetailService;
import com.berd.dev.services.CategorieDepenseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categorie-depense-details")
@RequiredArgsConstructor
public class CategorieDepenseDetailController {

    private final CategorieDepenseDetailService detailService;
    private final CategorieDepenseService categorieService;

    @GetMapping("/liste")
    public String liste(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Integer categorieId,
            @RequestParam(required = false, name = "all") String allParam,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        boolean all = false;
        if (allParam != null && !allParam.isEmpty() && allParam.equals("on"))
            all = true;
        Page<CategorieDepenseDetail> details = detailService.getFilteredDetails(search, categorieId, page, size , all);

        model.addAttribute("details", details);
        model.addAttribute("categories", categorieService.getAll());
        model.addAttribute("search", search);
        model.addAttribute("categorieId", categorieId);
        model.addAttribute("content", "pages/categorie-depense-details/categorie-depense-detail-liste");

        return "admin-layout";
    }

    @GetMapping("/saisie")
    public String saisie(@RequestParam(required = false) Integer categorieId, HttpSession session, Model model) {
        CategorieDepenseDetail detail;
        CategorieDepenseDetail detailForm = null;

        // Récupérer le formulaire de la session s'il existe
        if (session.getAttribute("detailForm") != null) {
            detailForm = (CategorieDepenseDetail) session.getAttribute("detailForm");
            detail = detailForm;
            session.removeAttribute("detailForm");
        } else {
            detail = new CategorieDepenseDetail();
            // Si un categorieId est passé en paramètre, le pré-remplir
            if (categorieId != null) {
                detail.setIdCategorieDepense(categorieId);
            }
        }

        model.addAttribute("detail", detail);
        model.addAttribute("detailForm", detailForm);
        model.addAttribute("categories", categorieService.getAllDto());
        // model.addAttribute("categories", List.of());

        model.addAttribute("content", "pages/categorie-depense-details/categorie-depense-detail-saisie");

        return "admin-layout";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, RedirectAttributes rd) {
        try {
            CategorieDepenseDetail detail = detailService.getDetailById(id)
                    .orElseThrow(() -> new RuntimeException("Détail non trouvé"));

            model.addAttribute("detail", detail);
            model.addAttribute("categories", categorieService.getAll());
            model.addAttribute("content", "pages/categorie-depense-details/categorie-depense-detail-saisie");

            return "admin-layout";
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Détail non trouvé");
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/categorie-depense-details/liste";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CategorieDepenseDetail detail, HttpSession session, RedirectAttributes rd) {
        try {
            // Validation backend
            if (detail.getIdCategorieDepense() == null) {
                // Conserver le formulaire en session
                session.setAttribute("detailForm", detail);
                rd.addFlashAttribute("toastMessage", "Veuillez sélectionner une catégorie dépense");
                rd.addFlashAttribute("toastType", "error");
                return "redirect:/categorie-depense-details/saisie";
            }

            detailService.saveDetail(detail);

            // Succès : supprimer le formulaire de la session
            session.removeAttribute("detailForm");

            String message = detail.getIdCategorieDepenseDetail() == null
                    ? "Détail ajouté avec succès"
                    : "Détail modifié avec succès";

            rd.addFlashAttribute("toastMessage", message);
            rd.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            e.printStackTrace();
            // Conserver le formulaire en session en cas d'erreur
            session.setAttribute("detailForm", detail);
            rd.addFlashAttribute("toastMessage", "Erreur lors de l'enregistrement: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/categorie-depense-details/saisie";
        }
        return "redirect:/categorie-depense-details/liste";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes rd) {
        try {
            detailService.deleteDetail(id);
            rd.addFlashAttribute("toastMessage", "Détail supprimé avec succès");
            rd.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Erreur lors de la suppression: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
        }
        return "redirect:/categorie-depense-details/liste";
    }

    @GetMapping("/fiche/{id}")
    public String fiche(@PathVariable Integer id, Model model, RedirectAttributes rd) {
        try {
            CategorieDepenseDetail detail = detailService.getDetailById(id)
                    .orElseThrow(() -> new RuntimeException("Détail non trouvé"));

            // Récupérer la catégorie parent si elle existe
            CategorieDepense categorieParent = null;
            if (detail.getIdCategorieDepense() != null) {
                categorieParent = categorieService.getById(detail.getIdCategorieDepense());
            }

            model.addAttribute("detail", detail);
            model.addAttribute("categorieParent", categorieParent);
            model.addAttribute("content", "pages/categorie-depense-details/categorie-depense-detail-fiche");
            return "admin-layout";
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Détail non trouvé");
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/categorie-depense-details/liste";
        }
    }
}
