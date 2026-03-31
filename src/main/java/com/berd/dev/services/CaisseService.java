package com.berd.dev.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berd.dev.forms.CaisseForm;
import com.berd.dev.models.Caisse;
import com.berd.dev.models.CaisseCategorie;
import com.berd.dev.models.User;
import com.berd.dev.repositories.CaisseCategoreRepository;
import com.berd.dev.repositories.CaisseRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CaisseService {
    private final CaisseRepository caisseRepository;
    private final CaisseCategoreRepository caisseCategoreRepository;

    private SecurityService securityService;
    public Caisse saveByForm(CaisseForm form) {
        User user = securityService.getAuthenticatedUser();
        if (form == null) {
            throw new IllegalArgumentException("CaisseForm cannot be null");
        }
        // Basic validation: ensure a category was selected
        if (form.getIdCaisseCategorie() == null) {
            throw new IllegalArgumentException("Veuillez sélectionner une catégorie pour la caisse");
        }
        if (form.getNom() == null || form.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la caisse ne peut pas être vide");
        }
        Caisse caisse = new Caisse();
        caisse.setNom(form.getNom());
        caisse.setDescription(form.getDescription());
        caisse.setUtilisateur(user);
        CaisseCategorie categorie = caisseCategoreRepository.findById(form.getIdCaisseCategorie())
                .orElseThrow(() -> new IllegalArgumentException(
                        "CaisseCategorie not found with id: " + form.getIdCaisseCategorie()));
        caisse.setCaisseCategorie(categorie);
        return caisseRepository.save(caisse);
    }

    public Caisse create(Caisse caisse) {
        if (caisse == null) {
            throw new IllegalArgumentException("Caisse cannot be null");
        }
        return caisseRepository.save(caisse);
    }

    public List<Caisse> getAll() {
        return caisseRepository.findAll();
    }
}
