package com.berd.dev.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaisseMvtForm {

    private Integer idCm;

    private Integer idCaisse;

    private Integer idDepense;

    private Double montant;

    private String type;

    private boolean fromDepense = false;

}
