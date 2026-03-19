package com.berd.dev.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.berd.dev.models.Param;

public interface ParamRepository extends JpaRepository<Param, Integer> {
    Optional<Param> findByLibelle(String libelle);
}
