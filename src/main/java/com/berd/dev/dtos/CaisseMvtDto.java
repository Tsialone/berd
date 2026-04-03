package com.berd.dev.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CaisseMvtDto {
        private Integer idCm;
        private Integer idCaisse;
        private String nomCaisse;

        private String type = "DEBIT";

        private Double montant;
        private Integer idDepense;
        private String nomDepense;
        private LocalDateTime dateHeureTransaction;
}
