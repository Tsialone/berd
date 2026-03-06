package com.berd.dev.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berd.dev.models.CategorieDepenseDetail;
import com.berd.dev.repositories.CategorieDepenseDetailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategorieDepenseDetailService {
    private final CategorieDepenseDetailRepository categorieDepenseDetailRepository;

    public List<CategorieDepenseDetail> getAll() {
        return categorieDepenseDetailRepository.findAll();
    }
}
