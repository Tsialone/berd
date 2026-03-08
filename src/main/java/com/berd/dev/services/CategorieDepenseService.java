package com.berd.dev.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.berd.dev.models.CategorieDepense;
import com.berd.dev.repositories.CategorieDepenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategorieDepenseService {
    private final CategorieDepenseRepository categorieDepenseRepository;

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
        return categorieDepenseRepository.save(categorieDepense);
    }

    public void deleteById(Integer id) {
        categorieDepenseRepository.deleteById(id);
    }

    public List<CategorieDepense> getByCriteria(String libelle) {
        System.out.println("libelle: " + libelle);
        System.out.println(categorieDepenseRepository.findByCriteria(libelle));
        return categorieDepenseRepository.findByCriteria(libelle);
    }

    public Page<CategorieDepense> getFilteredCategories(String search, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("libelle").ascending());

        if ((search != null && !search.trim().isEmpty()) || (type != null && !type.trim().isEmpty())) {
            return categorieDepenseRepository.findByFilters(
                    search != null ? search.trim() : "",
                    type != null ? type.trim() : "",
                    pageable);
        }

        return categorieDepenseRepository.findAll(pageable);
    }
}
