package com.berd.dev.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "caisse_categorie")
@Getter
@Setter
public class CaisseCategorie {
    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    @Column (name = "id_caisse_categorie")
    private Integer idCaisseCategorie;

    @Column(nullable = false)
    private String libelle;


    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    
}
