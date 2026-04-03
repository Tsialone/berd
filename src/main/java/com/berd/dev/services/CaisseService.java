package com.berd.dev.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.berd.dev.dtos.CaisseDto;
import com.berd.dev.forms.CaisseForm;
import com.berd.dev.mappers.CaisseMapper;
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

    private final SecurityService securityService;

    public List<CaisseDto> getLastTransaction(int nbrTransaction) {
        User utilisateur = securityService.getAuthenticatedUser();

        List<CaisseDto> resp = CaisseMapper
                .tDtos(caisseRepository.findByUtilisateurIdUtilisateur(utilisateur.getIdUtilisateur()));
        return resp.stream()
                .sorted(Comparator.comparing(CaisseDto::getCreated).reversed())
                .limit(nbrTransaction)
                .collect(Collectors.toList());
    }

    public List<CaisseDto> getAllDto() {
        User utilisateur = securityService.getAuthenticatedUser();
        return CaisseMapper.tDtos(caisseRepository.findByUtilisateurIdUtilisateur(utilisateur.getIdUtilisateur()));
    }

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
        
        Caisse caisse;
        
        // Si idCaisse est présent, il s'agit d'une modification
        if (form.getIdCaisse() != null) {
            caisse = caisseRepository.findById(form.getIdCaisse())
                    .orElseThrow(() -> new IllegalArgumentException("Caisse non trouvée avec l'id: " + form.getIdCaisse()));
        } else {
            // Sinon, c'est une création
            caisse = new Caisse();
            caisse.setUtilisateur(user);
        }
        
        caisse.setNom(form.getNom());
        caisse.setDescription(form.getDescription());
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

    public void delete(Integer idCaisse) 
    {   
        Caisse caisse = caisseRepository.findByIdWithCaisseMvt(idCaisse);
        
        if (caisse == null) {
            throw new IllegalArgumentException("Caisse non trouvée avec l'id: " + idCaisse);
        }
        
        // Vérifier si la caisse a des mouvements
        if (caisse.getCaisseMvts() != null && !caisse.getCaisseMvts().isEmpty()) {
            throw new IllegalArgumentException("Impossible de supprimer une caisse qui contient des mouvements. Veuillez d'abord supprimer les mouvements associés.");
        }
        
        caisseRepository.delete(caisse);
    }

    public Caisse getCaisseWithMvt(Integer idCaisse) {
        Caisse caisse = caisseRepository.findByIdWithCaisseMvt(idCaisse);
        if (caisse == null) {
            throw new IllegalArgumentException("Caisse non trouvée avec l'id: " + idCaisse);
        }
        return caisse;
    }

    public Caisse getCaisseById(Integer idCaisse) {
        return caisseRepository.findById(idCaisse)
                .orElseThrow(() -> new IllegalArgumentException("Caisse non trouvée avec l'id: " + idCaisse));
    }

    public List<CaisseDto> filterCaisses(Integer categorieId, LocalDate dateDebut, LocalDate dateFin, String search) {
        User utilisateur = securityService.getAuthenticatedUser();
        
        // Récupérer toutes les caisses de l'utilisateur
        List<CaisseDto> caisses = CaisseMapper
                .tDtos(caisseRepository.findByUtilisateurIdUtilisateur(utilisateur.getIdUtilisateur()));

        // Appliquer les filtres
        return caisses.stream()
                .filter(caisse -> {
                    // Filtre catégorie
                    if (categorieId != null) {
                        return caisse.getIdCaisseCategorie().equals(categorieId);
                    }
                    return true;
                })
                .filter(caisse -> {
                    // Filtre date début
                    if (dateDebut != null) {
                        LocalDateTime dateDebutDateTime = dateDebut.atStartOfDay();
                        return caisse.getCreated().isEqual(dateDebutDateTime) || caisse.getCreated().isAfter(dateDebutDateTime);
                    }
                    return true;
                })
                .filter(caisse -> {
                    // Filtre date fin
                    if (dateFin != null) {
                        LocalDateTime dateFinDateTime = dateFin.atTime(LocalTime.MAX);
                        return caisse.getCreated().isBefore(dateFinDateTime) || caisse.getCreated().isEqual(dateFinDateTime);
                    }
                    return true;
                })
                .filter(caisse -> {
                    // Filtre recherche (nom ou description)
                    if (search != null && !search.isEmpty()) {
                        String searchLower = search.toLowerCase();
                        return caisse.getNom().toLowerCase().contains(searchLower) ||
                               (caisse.getDescription() != null && caisse.getDescription().toLowerCase().contains(searchLower));
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
