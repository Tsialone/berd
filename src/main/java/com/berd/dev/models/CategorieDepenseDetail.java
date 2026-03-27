package com.berd.dev.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "categorie_depense_detail")
@Data
public class CategorieDepenseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cdd")
    private Integer idCategorieDepenseDetail;

    @Column(nullable = false)
    private String libelle;

    @Column
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "id_cd")
    private Integer idCategorieDepense;

    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

}
