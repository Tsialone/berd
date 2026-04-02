package com.berd.dev.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berd.dev.forms.CaisseMvtForm;
import com.berd.dev.models.Caisse;
import com.berd.dev.models.CaisseMvt;
import com.berd.dev.models.Depense;
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


    @Async
    public void refreshSoldeByIdCaisse (Integer idCaisse){
       Caisse caisse =  caisseRepository.findByIdWithCaisseMvt(idCaisse);
       caisse.refreshSolde();;
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
            throw new IllegalArgumentException("Le typ de mouvement doit ếtre débit ou crédit");

        Caisse caisse = caisseRepository.findById(form.getIdCaisse())
                .orElseThrow(() -> new IllegalArgumentException("La caisse doit existé"));

        Depense depense = null;
        if (form.getIdDepense() != null) {
            depense = depenseRepository.findById(form.getIdDepense())
                    .orElseThrow(() -> new IllegalArgumentException("Dépense introuvable"));
        }

        CaisseMvt mvt = new CaisseMvt();

        mvt.setCaisse(caisse);

        mvt.setCredit(form.getMontant());
        mvt.setDebit(0d);
        

        if (form.getType().equalsIgnoreCase("debit")) {
            mvt.setDebit(form.getMontant());
            mvt.setCredit(0d);

        }
        if (depense != null)
            mvt.setDepense(depense);
        double currSolde = caisse.getSolde() +  (mvt.getCredit()  - mvt.getDebit()) ;
        caisse.setSolde(currSolde);
        return caisseMvtRepository.save(mvt);
    }

    public java.util.List<CaisseMvt> getAll() {
        return caisseMvtRepository.findAll();
    }

}
