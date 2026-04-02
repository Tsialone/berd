package com.berd.dev.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berd.dev.dtos.DepenseDto;
import com.berd.dev.forms.DepenseDetailForm;
import com.berd.dev.forms.DepenseForm;
import com.berd.dev.mappers.DepenseMapper;
import com.berd.dev.models.CategorieDepense;
import com.berd.dev.models.CategorieDepenseDetail;
import com.berd.dev.models.Depense;
import com.berd.dev.models.DepenseDetail;
import com.berd.dev.models.Unite;
import com.berd.dev.models.User;
import com.berd.dev.repositories.CategorieDepenseDetailRepository;
import com.berd.dev.repositories.CategorieDepenseRepository;
import com.berd.dev.repositories.DepenseDetailRepository;
import com.berd.dev.repositories.DepenseRepository;
import com.berd.dev.repositories.UniteRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepenseService {

    private final DepenseRepository depenseRepository;
    private final DepenseDetailRepository depenseDetailRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;
    private final CategorieDepenseDetailRepository categorieDepenseDetailRepository;
    private final UniteRepository uniteRepository;
    private final SecurityService securityService;

    @Async
    public void refreshMontantTotalByIdDepense(Integer idDepense) {
        Depense depense = depenseRepository.findAllWithDepenseDetailsByIdDepense(idDepense);
        if (depense != null) {
            depense.refreshMontantTotal();
            depenseRepository.save(depense);
        }
    }

    @Async
    public void refreshMontantTotalDepenses() {
        for (Depense depense : depenseRepository.findAllWithDepenseDetails()) {
            refreshMontantTotalByIdDepense(depense.getIdDepense());
        }
    }

    public List<DepenseDto> getAllDto() {
        return DepenseMapper.toDtos(depenseRepository.findAll());
    }

    @Transactional
    public Depense save(DepenseForm form) {
        User user = securityService.getAuthenticatedUser();

        // Validation
        if (form.getIdCategorieDepense() == null) {
            throw new IllegalArgumentException("La catégorie de dépense est obligatoire");
        }

        if (form.getCreated() == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }

        // Récupérer la catégorie dépense
        CategorieDepense categorieDepense = categorieDepenseRepository.findById(form.getIdCategorieDepense())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie dépense introuvable"));

        // Créer ou récupérer la dépense
        Depense depense;
        if (form.getIdDepense() != null) {
            // Mode modification
            depense = depenseRepository.findById(form.getIdDepense())
                    .orElseThrow(() -> new IllegalArgumentException("Dépense introuvable"));

            // Supprimer les anciens détails
            if (depense.getDepenseDetails() != null && !depense.getDepenseDetails().isEmpty()) {
                depenseDetailRepository.deleteAll(depense.getDepenseDetails());
                depense.getDepenseDetails().clear();
            }
        } else {
            // Mode création
            depense = new Depense();
        }

        depense.setCategorieDepense(categorieDepense);
        depense.setCreated(form.getCreated());
        depense.setDescription(form.getDescription());
        depense.setEstPrevue(form.getEstPrevue() != null ? form.getEstPrevue() : false);
        depense.setUtilisateur(user);

        // Sauvegarder la dépense
        depense = depenseRepository.save(depense);

        // Traiter les détails
        if (form.getDetails() != null && !form.getDetails().isEmpty()) {
            List<DepenseDetail> details = new ArrayList<>();
            if (form.getDetails().isEmpty()) {
                throw new IllegalArgumentException("Au moins un détail de dépense est requis");
            }

            int ligneIndex = 1;
            for (DepenseDetailForm detailForm : form.getDetails()) {
                // Valider les champs obligatoires avec messages détaillés
                List<String> champsManquants = new ArrayList<>();

                if (detailForm.getIdCategorieDetail() == null) {
                    champsManquants.add("catégorie détail");
                }
                if (detailForm.getIdUnite() == null) {
                    champsManquants.add("unité");
                }
                if (detailForm.getQte() == null) {
                    champsManquants.add("quantité");
                }
                if (detailForm.getPu() == null) {
                    champsManquants.add("prix unitaire");
                }

                if (!champsManquants.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Ligne " + ligneIndex + " - Champ(s) obligatoire(s) manquant(s): " +
                                    String.join(", ", champsManquants));
                }

                // Récupérer les entités liées
                CategorieDepenseDetail categorieDetail = categorieDepenseDetailRepository
                        .findById(detailForm.getIdCategorieDetail())
                        .orElseThrow(() -> new IllegalArgumentException("Catégorie détail introuvable"));

                Unite unite = uniteRepository.findById(detailForm.getIdUnite())
                        .orElseThrow(() -> new IllegalArgumentException("Unité introuvable"));

                // Créer le détail
                DepenseDetail detail = new DepenseDetail();
                detail.setDepense(depense);
                detail.setCategorieDepenseDetail(categorieDetail);
                detail.setUnite(unite);
                detail.setQte(detailForm.getQte());

                detail.setPu(detailForm.getPu());
                if (detailForm.getDesignation() == null || detailForm.getDesignation().trim().isEmpty()) {
                    detailForm.setDesignation(
                            "depense detail " + depense.getDescription() + " pour " + categorieDetail.getLibelle()); // Valeur
                                                                                                                     // par
                                                                                                                     // défaut
                }
                detail.setDesignation(detailForm.getDesignation());
                detail.setCreated(LocalDateTime.now());

                details.add(detail);
                ligneIndex++;
            }

            // Sauvegarder tous les détails
            if (!details.isEmpty()) {
                depenseDetailRepository.saveAll(details);
                depense.setDepenseDetails(details);
            }
        }
        refreshMontantTotalByIdDepense(depense.getIdDepense());
        return depense;
    }

    public Page<Depense> getFilteredDepenses(Integer categorieId, Boolean estPrevue, LocalDate dateDebut,
            LocalDate dateFin, String search, int page, int size) {
        User user = securityService.getAuthenticatedUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));

        Specification<Depense> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // CONDITION DE SÉCURITÉ : On ne montre QUE les dépenses de cet utilisateur
            if (user != null) {
                predicates.add(cb.equal(root.get("utilisateur").get("idUtilisateur"), user.getIdUtilisateur()));
            } else {
                // Optionnel : Si pas d'utilisateur, on peut forcer un résultat vide
                return cb.disjunction();
            }
            if (categorieId != null) {
                predicates.add(cb.equal(root.get("categorieDepense").get("idCategorieDepense"), categorieId));
            }

            if (estPrevue != null) {
                predicates.add(cb.equal(root.get("estPrevue"), estPrevue));
            }

            if (dateDebut != null) {
                LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("created"), dateDebutTime));
            }

            if (dateFin != null) {
                LocalDateTime dateFinTime = dateFin.atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(root.get("created"), dateFinTime));
            }

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("categorieDepense").get("libelle")), pattern)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Depense> depensesPage = depenseRepository.findAll(spec, pageable);

        // Calculer le total pour chaque dépense
        depensesPage.forEach(depense -> {
            if (depense.getDepenseDetails() != null && !depense.getDepenseDetails().isEmpty()) {
                double total = depense.getDepenseDetails().stream()
                        .mapToDouble(detail -> detail.getQte() * detail.getPu())
                        .sum();
                depense.setMontantTotal(total);
            }
        });

        return depensesPage;
    }

    public double calculateTotal(List<Depense> depenses) {
        return depenses.stream()
                .mapToDouble(d -> d.getMontantTotal() != null ? d.getMontantTotal() : 0.0)
                .sum();
    }

    public List<Depense> getAllWithTotal() {
        List<Depense> depenses = depenseRepository.findAll();
        // Calculer le total pour chaque dépense
        depenses.forEach(depense -> {
            if (depense.getDepenseDetails() != null && !depense.getDepenseDetails().isEmpty()) {
                double total = depense.getDepenseDetails().stream()
                        .mapToDouble(detail -> detail.getQte() * detail.getPu())
                        .sum();
                depense.setMontantTotal(total);
            }
        });
        return depenses;
    }

    public Depense getById(Integer id) {
        return depenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dépense non trouvée avec l'id: " + id));
    }

    public void deleteById(Integer id) {
        depenseRepository.deleteById(id);
    }
}
