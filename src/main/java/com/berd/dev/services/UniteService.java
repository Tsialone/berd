package com.berd.dev.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berd.dev.models.Unite;
import com.berd.dev.repositories.UniteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UniteService {
    private final UniteRepository uniteRepository;


    public List<Unite>  getAll (){
        return uniteRepository.findAll();
    }
    
}
