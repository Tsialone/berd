package com.berd.dev.mappers;

import java.util.ArrayList;
import java.util.List;

import com.berd.dev.dtos.CaisseCategorieDto;
import com.berd.dev.models.CaisseCategorie;

public class CaisseCategorieMapper {
    public static CaisseCategorieDto toDto(CaisseCategorie caisseCategorie) {
        if (caisseCategorie == null) return null;

        CaisseCategorieDto resp = new CaisseCategorieDto();
        resp.setIdCaisseCategorie(caisseCategorie.getIdCaisseCategorie());
        resp.setLibelle(caisseCategorie.getLibelle());
        resp.setCreated(caisseCategorie.getCreated());
        return resp;
    }

    public static List<CaisseCategorieDto> tDtos(List<CaisseCategorie> categories) {
        List<CaisseCategorieDto> resp = new ArrayList<>();
        
        for (CaisseCategorie categorie : categories) {
            resp.add(toDto(categorie));
        }

        return resp;
    }
}
