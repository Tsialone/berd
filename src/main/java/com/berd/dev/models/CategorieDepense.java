package com.berd.dev.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name =  "categorie_depense")
@Data
public class CategorieDepense { 
    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    @Column(name =  "id_cd")
    private Integer idCategorieDepense;

    @Column (nullable =   false)
    private String libelle;

    
    @Column(nullable =  false)
    private String type;

   
}
