package com.berd.dev.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.berd.dev.models.CaisseMvt;

public interface CaisseMvtRepository extends JpaRepository<CaisseMvt, Integer> {

    @Query("SELECT cm FROM caisse_mvt cm WHERE cm.caisse.utilisateur.idUtilisateur = :id_utilisateur")
    List<CaisseMvt> findByIdUtilisateur(@Param("id_utilisateur") Integer id);


    List<CaisseMvt>    findByDepenseIdDepense(Integer idDepense);

}
