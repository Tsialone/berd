package com.berd.dev.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.berd.dev.models.CategorieDepenseDetail;
import com.berd.dev.repositories.CategorieDepenseDetailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategorieDepenseDetailService {
    private final CategorieDepenseDetailRepository categorieDepenseDetailRepository;


    public List<CategorieDepenseDetail> getByIdCategorieDepense  (Integer idCd ){
        if (idCd == null ) return new ArrayList<>();
        return  categorieDepenseDetailRepository.findByIdCategorieDepense(idCd) ;

    }
    public List<CategorieDepenseDetail> getAll() {
        
        return categorieDepenseDetailRepository.findAll();
    }
}
