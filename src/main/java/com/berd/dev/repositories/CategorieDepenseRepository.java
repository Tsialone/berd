package com.berd.dev.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.berd.dev.models.CategorieDepense;

public interface CategorieDepenseRepository extends JpaRepository<CategorieDepense, Integer> {

        @Query("SELECT cd FROM categorie_depense cd")
        List<CategorieDepense> findWithoutUser();

        List<CategorieDepense> findByLibelle(String libelle);

        @Query("SELECT cd FROM categorie_depense cd WHERE lower(cd.libelle) LIKE lower(CONCAT('%', :criteria, '%')) OR lower(cd.type) LIKE lower(CONCAT('%', :criteria, '%')) AND (:idUtilisateur = cd.utilisateur.idUtilisateur OR cd.utilisateur is null)")
        List<CategorieDepense> findByCriteria(@Param("criteria") String criteria,
                        @Param("idUtilisateur") Integer idUtilisateur);

        @Query("SELECT cd FROM categorie_depense cd WHERE " +
                        "(:search = '' OR lower(cd.libelle) LIKE lower(CONCAT('%', :search, '%'))) AND " +
                        "(:type = '' OR lower(cd.type) LIKE lower(CONCAT('%', :type, '%')))  AND " +
                        "(:idUtilisateur = cd.utilisateur.idUtilisateur) "
                )
        Page<CategorieDepense> findByFilters(@Param("search") String search, @Param("type") String type,
                        @Param("idUtilisateur") Integer idUtilisateur, Pageable pageable);


                         @Query("SELECT cd FROM categorie_depense cd WHERE " +
                        "(:search = '' OR lower(cd.libelle) LIKE lower(CONCAT('%', :search, '%'))) AND " +
                        "(:type = '' OR lower(cd.type) LIKE lower(CONCAT('%', :type, '%')))  AND " +
                        "(:idUtilisateur = cd.utilisateur.idUtilisateur OR cd.utilisateur is  NULL) "
                )
        Page<CategorieDepense> findByFiltersAll(@Param("search") String search, @Param("type") String type,
                        @Param("idUtilisateur") Integer idUtilisateur, Pageable pageable);

}
