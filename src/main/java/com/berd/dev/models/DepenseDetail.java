package com.berd.dev.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Entity(name = "depense_detail")
@Data
public class DepenseDetail {

    @Id
    @Column(name = "id_depense_detail")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDepenseDetail;

    @Column(nullable = false)
    private Double qte;

    @Column(nullable = false)
    private Double pu;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    @JoinColumn (name =  "id_unite")
    private Unite unite;

    @ManyToOne
    @JoinColumn (name =  "id_depense")
    private Depense depense;

    @ManyToOne
    @JoinColumn (name =  "id_cdd")
    private CategorieDepenseDetail categorieDepenseDetail;

    @Column
    private String designation;



}

