package com.berd.dev.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.berd.dev.models.CategorieDepense;
import com.berd.dev.models.CategorieDepenseDetail;
import com.berd.dev.models.User;
import com.berd.dev.repositories.CategorieDepenseDetailRepository;
import com.berd.dev.repositories.CategorieDepenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategorieDepenseDetailService {
    private final CategorieDepenseDetailRepository categorieDepenseDetailRepository;

    private final SecurityService securityService;

    public Integer[] getByIdUtilisateur(Integer idUtilisateur  , boolean all) {
        List<Integer> resp = new ArrayList<>();
        for (CategorieDepenseDetail cdd : getAll()) {
            if (cdd.getIdCategorieDepense() == null)
                continue;
            if (    cdd != null &&
                    ((cdd.getIdUtilisateur() == null && all) || (cdd.getIdUtilisateur() != null && cdd.getIdUtilisateur().equals((idUtilisateur))))
            ) {
                resp.add(cdd.getIdCategorieDepenseDetail());
            }

        }
        return resp.toArray(new Integer[0]);
    }

    public List<CategorieDepenseDetail> getByIdCategorieDepense(Integer idCd) {
        if (idCd == null)
            return new ArrayList<>();
        return categorieDepenseDetailRepository.findByIdCategorieDepense(idCd);
    }

    public List<CategorieDepenseDetail> getAll() {
        return categorieDepenseDetailRepository.findAll(Sort.by(Sort.Direction.ASC, "libelle"));
    }

    public Page<CategorieDepenseDetail> getFilteredDetails(String search, Integer categorieId, int page, int size , boolean all) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        User user = securityService.getAuthenticatedUser();
        // String categorieIds = getByIdUtilisateur(user.getIdUtilisateur());
        System.out.println("userId: " + user.getIdUtilisateur());
        Integer[] categorieIds = getByIdUtilisateur(user.getIdUtilisateur() , all);

        for (Integer x : categorieIds) {
            System.out.println("toto: " + x);

        }
        if ((search == null || search.trim().isEmpty()) && categorieId == null) {
            return categorieDepenseDetailRepository.findByFilters(
                    search != null ? search.trim() : "",
                    categorieId,
                    categorieIds,
                    pageable);
        }

        return categorieDepenseDetailRepository.findByFilters(
                search != null ? search.trim() : "",
                categorieId,
                categorieIds,
                pageable);
    }

    public Optional<CategorieDepenseDetail> getDetailById(Integer id) {
        return categorieDepenseDetailRepository.findById(id);
    }

    public CategorieDepenseDetail saveDetail(CategorieDepenseDetail detail) {
        User user = securityService.getAuthenticatedUser();
        detail.setIdUtilisateur(user.getIdUtilisateur());
        return categorieDepenseDetailRepository.save(detail);
    }

    public void deleteDetail(Integer id) throws Exception {
        User user = securityService.getAuthenticatedUser();
        if (id == null)
            throw new Exception("Id ne doit pas etre null pendant suppression categorieDetail");
        CategorieDepenseDetail cdd = categorieDepenseDetailRepository.findById(id).orElse(null);
        if (cdd == null)
            throw new Exception("Categorie depense detail non trouvée");
        if (cdd.getIdUtilisateur() == null || cdd.getIdUtilisateur() != null
                && !cdd.getIdUtilisateur().toString().equals(user.getIdUtilisateur().toString()))
            throw new Exception("Vous ne pouvez supprimez que votre propre categorie depense detail");
        categorieDepenseDetailRepository.deleteById(id);
    }
}
