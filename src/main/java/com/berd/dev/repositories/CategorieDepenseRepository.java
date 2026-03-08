package com.berd.dev.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.berd.dev.models.CategorieDepense;

public interface CategorieDepenseRepository extends JpaRepository<CategorieDepense, Integer> {

    List<CategorieDepense> findByLibelle(String libelle);

    @Query("SELECT cd FROM categorie_depense cd WHERE lower(cd.libelle) LIKE lower(CONCAT('%', :criteria, '%')) OR lower(cd.type) LIKE lower(CONCAT('%', :criteria, '%'))")
    List<CategorieDepense> findByCriteria(@Param("criteria") String criteria);

    @Query("SELECT cd FROM categorie_depense cd WHERE " +
            "(:search = '' OR lower(cd.libelle) LIKE lower(CONCAT('%', :search, '%'))) AND " +
            "(:type = '' OR lower(cd.type) LIKE lower(CONCAT('%', :type, '%')))")
    Page<CategorieDepense> findByFilters(@Param("search") String search, @Param("type") String type, Pageable pageable);

}
