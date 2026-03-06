package com.berd.dev.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.berd.dev.models.CategorieDepense;

public interface CategorieDepenseRepository extends JpaRepository<CategorieDepense, Integer> {

    List<CategorieDepense> findByLibelle(String libelle);

    @Query("SELECT cd FROM categorie_depense    cd WHERE  lower (cd.libelle) like lower  (CONCAT('%', :criteria, '%'))   or lower (cd.type) like  lower  (CONCAT('%', :criteria, '%')) ")
    List<CategorieDepense> findByCriteria(@Param("criteria") String criteria);

}
