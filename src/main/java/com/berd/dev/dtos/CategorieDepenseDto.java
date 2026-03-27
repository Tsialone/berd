package com.berd.dev.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CategorieDepenseDto {
    private Integer idCategorieDepense;
    private String libelle;
    private Integer idUtilisateur;

    public CategorieDepenseDto(
            Integer idCategorieDepense, String libelle, Integer idUtilisateur) {
        setIdCategorieDepense(idCategorieDepense);
        setIdUtilisateur(idUtilisateur);
        setLibelle(libelle);

    }
     public CategorieDepenseDto() {

    }

}