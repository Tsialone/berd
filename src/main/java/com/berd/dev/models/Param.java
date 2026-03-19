package com.berd.dev.models;

import java.beans.Transient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity (name = "param")
public class Param {
    @Id
    @Column (name = "id_param")
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    private Integer idParam;

    @Column (nullable = false , unique = true)
    private String libelle;


    @Column (nullable = false)
    private String valeur;


    @Transient
    public Long getValeurAsLong() {
        try {
            return Long.parseLong(this.valeur);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("La valeur du paramètre '" + this.libelle + "' n'est pas un nombre valide : " + this.valeur);
        }
    }
}
