package com.berd.dev.controllers.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.berd.dev.dtos.CategorieCourbeResponseDto;
import com.berd.dev.dtos.NombreDepenseResponseDto;
import com.berd.dev.dtos.RecapResponseDto;
import com.berd.dev.services.DepenseStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/depenses/stats")
@RequiredArgsConstructor
public class DepenseStatsRestController {

    private final DepenseStatsService depenseStatsService;

    @GetMapping("/recap")
    public ResponseEntity<RecapResponseDto> getRecap(
            @RequestParam(defaultValue = "mensuelle") String periode) {
        return ResponseEntity.ok(depenseStatsService.getRecapStats(periode));
    }

    @GetMapping("/courbe-categorie")
    public ResponseEntity<CategorieCourbeResponseDto> getCourbeCategorie(
            @RequestParam(defaultValue = "mensuelle") String periode) {
        return ResponseEntity.ok(depenseStatsService.getCourbeParCategorie(periode));
    }

    @GetMapping("/barres")
    public ResponseEntity<NombreDepenseResponseDto> getBarres(
            @RequestParam(defaultValue = "mensuelle") String periode) {
        return ResponseEntity.ok(depenseStatsService.getNombreDepenses(periode));
    }
}
