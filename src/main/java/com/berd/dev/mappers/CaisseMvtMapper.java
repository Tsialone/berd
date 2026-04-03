package com.berd.dev.mappers;

import java.util.List;

import com.berd.dev.dtos.CaisseMvtDto;
import com.berd.dev.models.CaisseMvt;

public class CaisseMvtMapper {
    public static CaisseMvtDto toDto (CaisseMvt caisseMvt) {
        if (caisseMvt == null) return null;

        CaisseMvtDto resp = new CaisseMvtDto();
        resp.setIdCm(caisseMvt.getIdCm());
        resp.setIdCaisse(caisseMvt.getCaisse().getIdCaisse());
        resp.setNomCaisse(caisseMvt.getCaisse().getNom());
        resp.setMontant(caisseMvt.getMontant());
        resp.setType(caisseMvt.getType());
        resp.setDateHeureTransaction(caisseMvt.getCreated());

        if (caisseMvt.getDepense() != null) {
            resp.setIdDepense(caisseMvt.getDepense().getIdDepense());
            resp.setNomDepense(caisseMvt.getDepense().getDescription());
        }

        return resp;
    }
    public static List<CaisseMvtDto> tDtos (List<CaisseMvt> caisseMvts) {
        return caisseMvts.stream().map(CaisseMvtMapper::toDto).toList();
    }
}
