package com.berd.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.berd.dev.models.Unite;

public interface UniteRepository  extends JpaRepository <Unite , Integer> {
    
}
