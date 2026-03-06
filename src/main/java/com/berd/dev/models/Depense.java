package com.berd.dev.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity(name = "depense")
@Data
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_depense")
    private Integer idDepense;

    @Column
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "est_prevue")
    private boolean estPrevue = false;

    @Column
    private String description;

    @OneToMany (mappedBy =  "depense")
    private List<DepenseDetail> depenseDetails;

    @ManyToOne
    @JoinColumn (name =   "id_cd")
    private CategorieDepense categorieDepense;
    
    @Transient
    private Double montantTotal;

}
