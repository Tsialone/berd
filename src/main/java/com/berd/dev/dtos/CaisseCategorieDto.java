package com.berd.dev.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CaisseCategorieDto {
    private Integer idCaisseCategorie;
    private String libelle;
    private LocalDateTime created;
}
