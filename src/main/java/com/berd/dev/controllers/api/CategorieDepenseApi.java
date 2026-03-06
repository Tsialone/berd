package com.berd.dev.controllers.api;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berd.dev.models.CategorieDepense;
import com.berd.dev.services.CategorieDepenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategorieDepenseApi {
    private final CategorieDepenseService categorieDepenseService;

    @GetMapping("/CategorieDepenses/{libelle}")
    public ResponseEntity<?> getListeCategorieDepense(@PathVariable String libelle) {
        try {
            List<CategorieDepense> categorieDepenses = categorieDepenseService.getByCriteria(libelle);
            return ResponseEntity.status(200).body(categorieDepenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}