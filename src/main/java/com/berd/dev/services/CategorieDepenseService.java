package com.berd.dev.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.berd.dev.dtos.CategorieDepenseDto;
import com.berd.dev.mappers.CategorieDepenseMapper;
import com.berd.dev.models.CategorieDepense;
import com.berd.dev.models.CategorieDepenseDetail;
import com.berd.dev.models.User;
import com.berd.dev.repositories.CategorieDepenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategorieDepenseService {
    private final CategorieDepenseRepository categorieDepenseRepository;

    private final SecurityService securityService;

    public List<CategorieDepenseDto> getAllDto() {
        return CategorieDepenseMapper.toDto(getAll());
    }

    public List<CategorieDepense> getAll() {
        return categorieDepenseRepository.findAll();
    }

    public Page<CategorieDepense> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("libelle").ascending());
        return categorieDepenseRepository.findAll(pageable);
    }

    public CategorieDepense getById(Integer id) {
        return categorieDepenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie dépense non trouvée avec l'ID : " + id));
    }

    public CategorieDepense save(CategorieDepense categorieDepense) {
        User user = securityService.getAuthenticatedUser();
        categorieDepense.setUtilisateur(user);
        return categorieDepenseRepository.save(categorieDepense);
    }

    public void deleteById(Integer id) throws Exception {
        User user = securityService.getAuthenticatedUser();
        if (id == null)
            throw new Exception("Id ne doit pas etre null pendant suppression categorie depense");
        CategorieDepense cd = categorieDepenseRepository.findById(id).orElse(null);
        if (cd == null)
            throw new Exception("Categorie depense non trouvée");
        if (cd.getUtilisateur() == null || cd.getUtilisateur() != null
                && !cd.getUtilisateur().getIdUtilisateur().equals(user.getIdUtilisateur()))
            throw new Exception("Vous ne pouvez supprimez que votre propre categorie depense");
        categorieDepenseRepository.deleteById(id);
    }

    public List<CategorieDepense> getByCriteria(String libelle) {
        User user = securityService.getAuthenticatedUser();
        // System.out.println("libelle: " + libelle);
        // System.out.println(categorieDepenseRepository.findByCriteria(libelle));
        return categorieDepenseRepository.findByCriteria(libelle, user.getIdUtilisateur());
    }

    public Page<CategorieDepense> getFilteredCategories(String search, String type, int page, int size, boolean all) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("libelle").ascending());
        User user = securityService.getAuthenticatedUser();

        if ((search != null && !search.trim().isEmpty()) || (type != null && !type.trim().isEmpty())) {
            if (all) {
                return categorieDepenseRepository.findByFiltersAll(
                        search != null ? search.trim() : "",
                        type != null ? type.trim() : "",
                        user.getIdUtilisateur(),
                        pageable);
            } else {
                return categorieDepenseRepository.findByFilters(
                        search != null ? search.trim() : "",
                        type != null ? type.trim() : "",
                        user.getIdUtilisateur(),
                        pageable);
            }

        }
         if (all) {
                 return categorieDepenseRepository.findByFiltersAll(
                "", // search vide
                "", // type vide
                user.getIdUtilisateur(),
                pageable);
            } else {
                return categorieDepenseRepository.findByFilters(
                "", // search vide
                "", // type vide
                user.getIdUtilisateur(),
                pageable);
            }

       
    }
}
