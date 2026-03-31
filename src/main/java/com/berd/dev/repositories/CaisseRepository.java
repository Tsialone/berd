package com.berd.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.berd.dev.models.Caisse;

public interface CaisseRepository extends JpaRepository <Caisse , Integer> {
    
}
