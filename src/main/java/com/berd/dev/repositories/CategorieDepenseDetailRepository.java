package com.berd.dev.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.berd.dev.models.CategorieDepenseDetail;

public interface CategorieDepenseDetailRepository extends JpaRepository<CategorieDepenseDetail, Integer> {
    List<CategorieDepenseDetail> findByIdCategorieDepense(Integer idCd);

    @Query("SELECT cdd FROM categorie_depense_detail cdd WHERE " +
            "cdd.idCategorieDepense IN :categorieIds AND " +
            "(:search = '' OR LOWER(cdd.libelle) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:categorieId IS NULL OR cdd.idCategorieDepense = :categorieId)"
        )
    Page<CategorieDepenseDetail> findByFilters(
            @Param("search") String search,
            @Param("categorieId") Integer categorieId,
            @Param ("categorieIds") Integer [] categorieIds,
            Pageable pageable);
}
