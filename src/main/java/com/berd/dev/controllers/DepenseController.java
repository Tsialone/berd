package com.berd.dev.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.berd.dev.forms.DepenseDetailForm;
import com.berd.dev.forms.DepenseForm;
import com.berd.dev.models.Depense;
import com.berd.dev.services.CategorieDepenseDetailService;
import com.berd.dev.services.CategorieDepenseService;
import com.berd.dev.services.DepenseService;
import com.berd.dev.services.UniteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/depenses")
@RequiredArgsConstructor
public class DepenseController {

    private final CategorieDepenseService categorieDepenseService;
    private final CategorieDepenseDetailService categorieDepenseDetailService;
    private final UniteService uniteService;
    private final DepenseService depenseService;

    @GetMapping("/liste")
    public String liste(
            @RequestParam(required = false) Integer categorieId,
            @RequestParam(required = false) Boolean estPrevue,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Depense> depensesPage = depenseService.getFilteredDepenses(
                categorieId, estPrevue, dateDebut, dateFin, search, page, size);

        model.addAttribute("depenses", depensesPage);
        model.addAttribute("categories", categorieDepenseService.getAll());
        model.addAttribute("totalGeneral", depenseService.calculateTotal(depensesPage.getContent()));
        model.addAttribute("content", "pages/depenses/depense-liste");
        return "admin-layout";
    }

    @GetMapping("/saisie")
    public String form(Model model, jakarta.servlet.http.HttpSession session) {

        model.addAttribute("categoriesDetail", categorieDepenseDetailService.getAll());
        model.addAttribute("categories", categorieDepenseService.getAllDto());
        model.addAttribute("unites", uniteService.getAll());

        // Récupérer le formulaire de la session s'il existe
        DepenseForm depenseForm = (DepenseForm) session.getAttribute("depenseForm");
        if (depenseForm != null) {
            model.addAttribute("depenseForm", depenseForm);
            // Ne PAS supprimer ici, on garde en session jusqu'à sauvegarde réussie
        }

        model.addAttribute("content", "pages/depenses/depense-saisie");
        return "admin-layout";
    }

    @GetMapping("/annuler")
    public String annuler(jakarta.servlet.http.HttpSession session, RedirectAttributes rd) {
        // Nettoyer la session
        session.removeAttribute("depenseForm");

        rd.addFlashAttribute("toastMessage", "Opération annulée");
        rd.addFlashAttribute("toastType", "info");

        return "redirect:/depenses/liste";
    }

    @PostMapping("/save")
    public String insert(@ModelAttribute DepenseForm form, RedirectAttributes rd,
            jakarta.servlet.http.HttpSession session) {
        try {
            // Validation basique
            if (form.getDetails() == null || form.getDetails().isEmpty()) {
                throw new IllegalArgumentException("Veuillez ajouter au moins un détail de dépense");
            }

            // Sauvegarder la dépense avec ses détails
            depenseService.save(form);

            // System.out.println(form.getDetails().size() + " taillle ");

            // Succès : supprimer le formulaire de la session
            session.removeAttribute("depenseForm");

            rd.addFlashAttribute("toastMessage", "Dépense insérée avec succès");
            rd.addFlashAttribute("toastType", "success");

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Passer le formulaire en session pour conserver l'état
            session.setAttribute("depenseForm", form);
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "warning");
        } catch (Exception e) {
            e.printStackTrace();
            // Passer le formulaire en session pour conserver l'état
            session.setAttribute("depenseForm", form);
            rd.addFlashAttribute("toastMessage", "Erreur lors de l'insertion: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
        }

        return "redirect:/depenses/saisie";
    }

    @GetMapping("/modifier/{id}")
    public String modifier(@PathVariable Integer id, Model model, jakarta.servlet.http.HttpSession session) {
        // Charger la dépense existante
        Depense depense = depenseService.getById(id);

        // Convertir en DepenseForm
        DepenseForm depenseForm = new DepenseForm();
        depenseForm.setIdDepense(depense.getIdDepense());
        depenseForm.setIdCategorieDepense(depense.getCategorieDepense().getIdCategorieDepense());
        depenseForm.setCreated(depense.getCreated());
        depenseForm.setDescription(depense.getDescription());
        depenseForm.setEstPrevue(depense.isEstPrevue());

        // Charger les détails
        if (depense.getDepenseDetails() != null && !depense.getDepenseDetails().isEmpty()) {
            List<DepenseDetailForm> details = new ArrayList<>();
            for (com.berd.dev.models.DepenseDetail detail : depense.getDepenseDetails()) {
                DepenseDetailForm detailForm = new DepenseDetailForm();
                detailForm.setIdCategorieDetail(detail.getCategorieDepenseDetail().getIdCategorieDepenseDetail());
                detailForm.setDesignation(detail.getDesignation());
                detailForm.setQte(detail.getQte());
                detailForm.setIdUnite(detail.getUnite().getIdUnite());
                detailForm.setPu(detail.getPu());
                details.add(detailForm);
            }
            depenseForm.setDetails(details);
        }

        model.addAttribute("categoriesDetail", categorieDepenseDetailService.getAll());
        model.addAttribute("categories", categorieDepenseService.getAll());
        model.addAttribute("unites", uniteService.getAll());
        model.addAttribute("depenseForm", depenseForm);
        model.addAttribute("content", "pages/depenses/depense-saisie");

        return "admin-layout";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes rd) {
        try {
            depenseService.deleteById(id);
            rd.addFlashAttribute("toastMessage", "Dépense supprimée avec succès");
            rd.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Erreur lors de la suppression");
            rd.addFlashAttribute("toastType", "error");
        }
        return "redirect:/depenses/liste";
    }

    @GetMapping("/fiche/{id}")
    public String fiche(@PathVariable Integer id, Model model) {
        Depense depense = depenseService.getById(id);
        // Calculer le montant total
        if (depense.getDepenseDetails() != null && !depense.getDepenseDetails().isEmpty()) {
            double total = depense.getDepenseDetails().stream()
                    .mapToDouble(detail -> detail.getQte() * detail.getPu())
                    .sum();
            depense.setMontantTotal(total);
        }
        model.addAttribute("depense", depense);
        model.addAttribute("content", "pages/depenses/depense-fiche");
        return "admin-layout";
    }

     @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("content", "pages/depenses/depense-stats");
        return "admin-layout";
    }

}
