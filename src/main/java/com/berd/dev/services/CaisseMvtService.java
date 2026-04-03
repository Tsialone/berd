package com.berd.dev.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berd.dev.dtos.CaisseMvtDto;
import com.berd.dev.forms.CaisseMvtForm;
import com.berd.dev.mappers.CaisseMvtMapper;
import com.berd.dev.models.Caisse;
import com.berd.dev.models.CaisseMvt;
import com.berd.dev.models.Depense;
import com.berd.dev.models.User;
import com.berd.dev.repositories.CaisseMvtRepository;
import com.berd.dev.repositories.CaisseRepository;
import com.berd.dev.repositories.DepenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CaisseMvtService {

    private final CaisseMvtRepository caisseMvtRepository;
    private final CaisseRepository caisseRepository;
    private final DepenseRepository depenseRepository;

    private final SecurityService securityService;

    public Double getTotalDebitbyIdDepense(Integer idDepense) {
        return caisseMvtRepository.findByDepenseIdDepense(idDepense).stream()
                .mapToDouble(CaisseMvt::getDebit)
                .sum();
    }

    public List<CaisseMvtDto> getLastTransaction(int nbrTransaction) {
        User utilisateur = securityService.getAuthenticatedUser();
        List<CaisseMvt> filtred = caisseMvtRepository.findByIdUtilisateur(utilisateur.getIdUtilisateur()).stream()
                .sorted((m1, m2) -> m2.getCreated().compareTo(m1.getCreated()))
                .limit(nbrTransaction)
                .toList();
        return CaisseMvtMapper.tDtos(filtred);
    }

    public CaisseMvtForm genererCaisseMvtFormByIdDepense(Integer idDepense) {
        Depense depense = depenseRepository.findById(idDepense)
                .orElseThrow(() -> new IllegalArgumentException("Dépense introuvable"));
        CaisseMvtForm form = new CaisseMvtForm();
        form.setIdDepense(idDepense);
        form.setMontant(depense.getMontantTotal());
        form.setType("DEBIT");
        form.setFromDepense(true);
        return form;
    }

    @Async
    public void refreshSoldeByIdCaisse(Integer idCaisse) {
        Caisse caisse = caisseRepository.findByIdWithCaisseMvt(idCaisse);
        caisse.refreshSolde();
        ;
        caisseRepository.save(caisse);
    }

    @Transactional
    public CaisseMvt saveByForm(CaisseMvtForm form) {
        if (form == null)
            throw new IllegalArgumentException("Formulaire invalide");
        if (form.getIdCaisse() == null)
            throw new IllegalArgumentException("Veuillez sélectionner une caisse");
        if (form.getMontant() == null || form.getMontant() <= 0d)
            throw new IllegalArgumentException("Le montant doit être positif");
        if (form.getType() == null || form.getType().isEmpty())
            throw new IllegalArgumentException("Le type de mouvement doit ếtre débit ou crédit");

        Caisse caisse = caisseRepository.findById(form.getIdCaisse())
                .orElseThrow(() -> new IllegalArgumentException("La caisse doit existé"));

        Depense depense = null;
        if (form.getIdDepense() != null) {
            depense = depenseRepository.findById(form.getIdDepense())
                    .orElseThrow(() -> new IllegalArgumentException("Dépense introuvable"));
        }

        CaisseMvt mvt;
        
        // Si idCm est fourni, on modifie le mouvement existant
        if (form.getIdCm() != null) {
            mvt = caisseMvtRepository.findById(form.getIdCm())
                    .orElseThrow(() -> new IllegalArgumentException("Mouvement non trouvé"));
            
            // Annuler l'effet de l'ancien mouvement
            if (mvt.getDepense() != null) {
                Depense oldDepense = mvt.getDepense();
                oldDepense.setTotalPayer(Math.max(oldDepense.getTotalPayer() - mvt.getMontant(), 0d));
                depenseRepository.save(oldDepense);
            }
            caisse.setSolde(caisse.getSolde() - (mvt.getCredit() - mvt.getDebit()));
        } else {
            // Sinon, créer un nouveau mouvement
            mvt = new CaisseMvt();
        }

        mvt.setCaisse(caisse);
        mvt.setCredit(form.getMontant());
        mvt.setDebit(0d);

        if (form.getType().equalsIgnoreCase("debit")) {
            mvt.setDebit(form.getMontant());
            mvt.setCredit(0d);
        }
        
        if (depense != null) {
            Double totalPayer = depense.getTotalPayer() + mvt.getMontant();
            Double resteAPayer = depense.getMontantTotal() - depense.getTotalPayer();

            if (totalPayer > depense.getMontantTotal()) {
                throw new IllegalArgumentException("Le montant dépasse le total de la dépense il vous reste: " + resteAPayer + " Ar" );
            }
            depense.setTotalPayer(totalPayer);
            mvt.setDepense(depense);
        }

        double currSolde = caisse.getSolde() + (mvt.getCredit() - mvt.getDebit());
        caisse.setSolde(currSolde);
        return caisseMvtRepository.save(mvt);
    }

    public java.util.List<CaisseMvt> getAll() {
        return caisseMvtRepository.findAll();
    }

    public CaisseMvt getMvtById(Integer idCm) {
        return caisseMvtRepository.findById(idCm)
                .orElseThrow(() -> new IllegalArgumentException("Mouvement non trouvé avec l'id: " + idCm));
    }

    public List<CaisseMvtDto> filterMouvements(Integer caisseId, String type, LocalDate dateDebut, LocalDate dateFin) {
        List<CaisseMvt> mouvements = caisseMvtRepository.findAll();

        return mouvements.stream()
                .filter(mvt -> {
                    if (caisseId != null) {
                        return mvt.getCaisse().getIdCaisse().equals(caisseId);
                    }
                    return true;
                })
                .filter(mvt -> {
                    if (type != null && !type.isEmpty()) {
                        return mvt.getType().equalsIgnoreCase(type);
                    }
                    return true;
                })
                .filter(mvt -> {
                    if (dateDebut != null) {
                        LocalDateTime dateDebutDateTime = dateDebut.atStartOfDay();
                        return mvt.getCreated().isEqual(dateDebutDateTime) || mvt.getCreated().isAfter(dateDebutDateTime);
                    }
                    return true;
                })
                .filter(mvt -> {
                    if (dateFin != null) {
                        LocalDateTime dateFinDateTime = dateFin.atTime(LocalTime.MAX);
                        return mvt.getCreated().isBefore(dateFinDateTime) || mvt.getCreated().isEqual(dateFinDateTime);
                    }
                    return true;
                })
                .map(CaisseMvtMapper::toDto)
                .toList();
    }

    public void delete(Integer idCm) {
        CaisseMvt mvt = caisseMvtRepository.findById(idCm)
                .orElseThrow(() -> new IllegalArgumentException("Mouvement non trouvé avec l'id: " + idCm));

        // Si lié à une dépense, mettre à jour le totalPayer
        if (mvt.getDepense() != null) {
            Depense depense = mvt.getDepense();
            Double newTotalPayer = depense.getTotalPayer() - mvt.getMontant();
            depense.setTotalPayer(Math.max(newTotalPayer, 0d));
            depenseRepository.save(depense);
        }

        // Mettre à jour le solde de la caisse
        Caisse caisse = mvt.getCaisse();
        Double newSolde = caisse.getSolde() - (mvt.getCredit() - mvt.getDebit());
        caisse.setSolde(newSolde);
        caisseRepository.save(caisse);

        // Supprimer le mouvement
        caisseMvtRepository.delete(mvt);
    }
}