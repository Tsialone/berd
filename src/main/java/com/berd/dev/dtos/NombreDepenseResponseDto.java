package com.berd.dev.dtos;

import java.util.List;

public record NombreDepenseResponseDto(
        String periode,
        String periodeLabel,
        List<NombrePointDto> nbrDepenseParCategorie,
        List<NombrePointDto> nbrDepenseParSousCategorie) {
}
