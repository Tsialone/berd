package com.berd.dev.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CaisseDto {
    private Integer idCaisse;
    private String nom;

    private Integer idCaisseCategorie;
    private String categorie;
    private LocalDateTime created;
    private Double solde;
    private String description;
}
