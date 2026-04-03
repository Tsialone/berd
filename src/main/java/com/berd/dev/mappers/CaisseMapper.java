package com.berd.dev.mappers;

import java.util.ArrayList;
import java.util.List;

import com.berd.dev.dtos.CaisseDto;
import com.berd.dev.models.Caisse;

public class CaisseMapper {
    public static CaisseDto toDto  (Caisse caisse ){

        CaisseDto resp = new CaisseDto();
        if (caisse ==null) return null;

        resp.setNom(caisse.getNom());
        resp.setIdCaisse(caisse.getIdCaisse());
        resp.setIdCaisseCategorie(caisse.getCaisseCategorie().getIdCaisseCategorie());
        resp.setCategorie(caisse.getCaisseCategorie().getLibelle());
        resp.setCreated(caisse.getCreated());
        resp.setSolde(caisse.getSolde());
        return resp;
    }

    public static List<CaisseDto> tDtos  (List<Caisse> caisses) {
        List<CaisseDto> resp = new ArrayList<>();
        
        for (Caisse caisse : caisses) {
                resp.add(toDto(caisse));
        }

        return resp;
    }
}
