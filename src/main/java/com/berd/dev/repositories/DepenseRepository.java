package com.berd.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.berd.dev.models.Depense;

public interface DepenseRepository extends JpaRepository<Depense, Integer>, JpaSpecificationExecutor<Depense> {
    
}
