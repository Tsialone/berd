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

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categorie-depenses")
@RequiredArgsConstructor
public class CategorieDepenseController {

    private final CategorieDepenseService categorieDepenseService;
    private final CategorieDepenseDetailService categorieDepenseDetailService;

    @GetMapping("/liste")
    public String liste(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, name = "all") String allParam,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        boolean all = false;
        if (allParam != null && !allParam.isEmpty() && allParam.equals("on"))
            all = true;
        Page<CategorieDepense> categoriesPage = categorieDepenseService.getFilteredCategories(search, type, page, size,
                all);
        System.out.println("allParam: " + all);

        model.addAttribute("categories", categoriesPage);
        model.addAttribute("all", allParam);
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("content", "pages/categorie-depenses/categorie-depense-liste");
        return "admin-layout";
    }

    @GetMapping("/saisie")
    public String formAjout(Model model) {
        model.addAttribute("categorieDepense", new CategorieDepense());
        model.addAttribute("isEdit", false);
        model.addAttribute("content", "pages/categorie-depenses/categorie-depense-saisie");

        return "admin-layout";
    }

    @GetMapping("/edit/{id}")
    public String formEdit(@PathVariable Integer id, Model model, RedirectAttributes rd) {
        try {
            CategorieDepense categorieDepense = categorieDepenseService.getById(id);
            model.addAttribute("categorieDepense", categorieDepense);
            model.addAttribute("isEdit", true);
            model.addAttribute("content", "pages/categorie-depenses/categorie-depense-saisie");
            return "admin-layout";
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Catégorie non trouvée");
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/categorie-depenses/liste";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CategorieDepense categorieDepense, RedirectAttributes rd) {
        try {
            categorieDepenseService.save(categorieDepense);

            String message = categorieDepense.getIdCategorieDepense() == null
                    ? "Catégorie ajoutée avec succès"
                    : "Catégorie modifiée avec succès";

            rd.addFlashAttribute("toastMessage", message);
            rd.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            e.printStackTrace();
            rd.addFlashAttribute("toastMessage", "Erreur lors de l'enregistrement: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
        }
        return "redirect:/categorie-depenses/liste";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes rd) {
        try {
            CategorieDepense categorieDepense = categorieDepenseService.getById(id);
            if (categorieDepense == null)
                throw new Exception("Catégorie dépense non trouvée avec l'ID : " + id);
            List<CategorieDepenseDetail> details = categorieDepenseDetailService.getByIdCategorieDepense(id);
            if (details != null && !details.isEmpty()) {
                throw new Exception(
                        "Impossible de supprimer cette catégorie car elle est associée à des détails de dépense.");
            }
            categorieDepenseService.deleteById(id);

            rd.addFlashAttribute("toastMessage", "Catégorie supprimée avec succès");
            rd.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Erreur lors de la suppression: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
        }
        return "redirect:/categorie-depenses/liste";
    }

    @GetMapping("/fiche/{id}")
    public String fiche(@PathVariable Integer id, Model model, RedirectAttributes rd) {
        try {
            CategorieDepense categorieDepense = categorieDepenseService.getById(id);
            List<CategorieDepenseDetail> details = categorieDepenseDetailService.getByIdCategorieDepense(id);

            model.addAttribute("categorieDepense", categorieDepense);
            model.addAttribute("details", details);
            model.addAttribute("content", "pages/categorie-depenses/categorie-depense-fiche");
            return "admin-layout";
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Catégorie non trouvée");
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/categorie-depenses/liste";
        }
    }
}
