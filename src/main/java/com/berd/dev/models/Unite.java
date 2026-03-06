package com.berd.dev.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "unite")
@Data
public class Unite {
    @Id
    @Column(name = "id_unite")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUnite;

    @Column(nullable = false)
    private String libelle;


}
