package com.berd.dev.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.berd.dev.models.CategorieDepenseDetail;

public interface CategorieDepenseDetailRepository extends JpaRepository<CategorieDepenseDetail, Integer> {
    List<CategorieDepenseDetail> findByIdCategorieDepense(Integer idCd);
}
