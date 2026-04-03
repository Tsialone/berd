package com.berd.dev.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import com.berd.dev.dtos.CaisseMvtDto;
import com.berd.dev.forms.CaisseMvtForm;
import com.berd.dev.mappers.CaisseMvtMapper;
import com.berd.dev.models.CaisseMvt;
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
    public String liste(Model model, @RequestParam(required = false) Integer caisseId, 
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) LocalDate dateDebut,
                        @RequestParam(required = false) LocalDate dateFin) {
        model.addAttribute("mouvements", caisseMvtService.filterMouvements(caisseId, type, dateDebut, dateFin));
        model.addAttribute("caisses", caisseService.getAllDto());
        model.addAttribute("content", "pages/caisses/caisse-mvt-liste");
        return "admin-layout";
    }

    @GetMapping("/saisie")
    public String saisie(Model model, jakarta.servlet.http.HttpSession session , @RequestParam(required = false) Integer idCaisse) {
        model.addAttribute("caisses", caisseService.getAllDto());
        model.addAttribute("depenses", depenseService.getAllDto());
        model.addAttribute("lastMvts", caisseMvtService.getLastTransaction(5));

        CaisseMvtForm form = (CaisseMvtForm) session.getAttribute("caisseMvtForm");
        if (form != null) {
            model.addAttribute("caisseMvtForm", form);
        } else {
            form = new CaisseMvtForm();
            if (idCaisse != null) form.setIdCaisse(idCaisse);
            model.addAttribute("caisseMvtForm", form);
        }

        model.addAttribute("content", "pages/caisses/caisse-mvt-saisie");
        return "admin-layout";
    }

    @GetMapping("/fiche/{id}")
    public String fiche(@PathVariable Integer id, Model model, RedirectAttributes rd) {
        try {
            CaisseMvt mvt = caisseMvtService.getMvtById(id);
            model.addAttribute("mouvement", mvt);
            model.addAttribute("content", "pages/caisses/caisse-mvt-fiche");
            return "admin-layout";
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Mouvement non trouvé");
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/caisses-mvts/liste";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, HttpSession session, RedirectAttributes rd) {
        try {
            CaisseMvt mvt = caisseMvtService.getMvtById(id);
            CaisseMvtDto mvtDto = CaisseMvtMapper.toDto(mvt);
            CaisseMvtForm form = (CaisseMvtForm) session.getAttribute("caisseMvtForm");
            if (form == null) {
                form = new CaisseMvtForm();
                form.setIdCm(id);
                form.setIdCaisse(mvtDto.getIdCaisse());
                form.setType(mvtDto.getType());
                form.setMontant(mvtDto.getMontant());
                if (mvtDto.getIdDepense() != null) {
                    form.setIdDepense(mvtDto.getIdDepense());
                }
            }
            model.addAttribute("caisseMvtForm", form);
            model.addAttribute("caisses", caisseService.getAllDto());
            model.addAttribute("depenses", depenseService.getAllDto());
            model.addAttribute("content", "pages/caisses/caisse-mvt-edit");
            return "admin-layout";
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Mouvement non trouvé");
            rd.addFlashAttribute("toastType", "error");
            return "redirect:/caisses-mvts/liste";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes rd) {
        try {
            caisseMvtService.delete(id);
            rd.addFlashAttribute("toastMessage", "Mouvement supprimé avec succès");
            rd.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            rd.addFlashAttribute("toastMessage", e.getMessage());
            rd.addFlashAttribute("toastType", "warning");
        } catch (Exception e) {
            rd.addFlashAttribute("toastMessage", "Erreur lors de la suppression: " + e.getMessage());
            rd.addFlashAttribute("toastType", "error");
        }
        return "redirect:/caisses-mvts/liste";
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
