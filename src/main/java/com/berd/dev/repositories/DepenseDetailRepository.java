package com.berd.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.berd.dev.models.DepenseDetail;

public interface DepenseDetailRepository extends JpaRepository<DepenseDetail, Integer> {
    
}
