package com.berd.dev.forms;


import lombok.Data;

@Data
public class CaisseForm {
    private Integer idCaisse;
    private String nom;
    private String description;

    private Integer idCaisseCategorie;

   

}
