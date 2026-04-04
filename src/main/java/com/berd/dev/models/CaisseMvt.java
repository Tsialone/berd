package com.berd.dev.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity (name = "caisse_mvt")
@Getter
@Setter
public class CaisseMvt {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column  (name =  "id_cm")
    private Integer idCm;


    @Column (name =  "debit"  , nullable =  false) 
    private Double debit = 0d;

    @Column (name =  "credit" , nullable =  false)
    private Double credit = 0d;


    @OneToOne
    @JoinColumn (name =  "id_caisse" , nullable =  false)
    private Caisse caisse ;

    @OneToOne
    @JoinColumn (name =  "id_depense" , nullable =  true)
    private Depense depense;
    
    private String description;

    private LocalDateTime created = LocalDateTime.now();

    public Double getMontant() {
        if (debit > 0) {
            return debit;
        } else if (credit > 0) {
            return credit;
        } else {
            return 0d;
        }
    }

    public String getType() {
        if (debit > 0) {
            return "DEBIT";
        } else if (credit > 0) {
            return "CREDIT";
        } else {
            return "NEUTRE";
        }
    }
    public User getUtilisateur() {
        return caisse.getUtilisateur();
    }

    

}
