package com.berd.dev.mappers;

import java.util.ArrayList;
import java.util.List;

import com.berd.dev.dtos.CategorieDepenseDto;
import com.berd.dev.models.CategorieDepense;

public class CategorieDepenseMapper {
    public static  CategorieDepenseDto toDto (CategorieDepense categorieDepense) {
        CategorieDepenseDto resp = new CategorieDepenseDto();

        if (categorieDepense == null) return null;

        resp.setIdCategorieDepense(categorieDepense.getIdCategorieDepense());
        resp.setLibelle(categorieDepense.getLibelle());
        if (categorieDepense.getUtilisateur() != null)   resp.setIdUtilisateur(categorieDepense.getUtilisateur().getIdUtilisateur());
        
        return resp;
    }
      public static  List<CategorieDepenseDto> toDto (List<CategorieDepense> categorieDepenses) {
        List<CategorieDepenseDto> resp = new ArrayList<>();

        for (CategorieDepense categorieDepense : categorieDepenses) {
            resp.add(toDto(categorieDepense));
        }
        
        return resp;
    }

}
