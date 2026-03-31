package com.berd.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.berd.dev.models.CaisseCategorie;

public interface CaisseCategoreRepository extends JpaRepository <CaisseCategorie , Integer> {
    
}
