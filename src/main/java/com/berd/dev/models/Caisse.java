package com.berd.dev.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity (name = "caisse")
@Getter
@Setter
public class Caisse {
    @Id
    @Column(name = "id_caisse")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCaisse;


    @Column(nullable = false )
    private String nom;


    private String description;

    private LocalDateTime created = LocalDateTime.now();

    private Double solde = 0d;


    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caisse_categorie", nullable = false)
    private CaisseCategorie caisseCategorie;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private User utilisateur;



}
