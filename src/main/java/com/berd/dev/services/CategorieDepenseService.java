package com.berd.dev.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berd.dev.models.CategorieDepense;
import com.berd.dev.repositories.CategorieDepenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategorieDepenseService  {
    private final CategorieDepenseRepository categorieDepenseRepository;



    public List<CategorieDepense>  getAll (){
        return categorieDepenseRepository.findAll();
    }

    public List<CategorieDepense> getByCriteria (String libelle ) {
        System.out.println("libelle: " + libelle);
        System.out.println(categorieDepenseRepository.findByCriteria(libelle));
        return categorieDepenseRepository.findByCriteria(libelle);

    }
}
