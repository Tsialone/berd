package com.berd.dev.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity(name = "categorie_depense")
@Data
public class CategorieDepense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cd")
    private Integer idCategorieDepense;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String type;

    @Column (name =  "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();

   

}
