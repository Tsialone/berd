package com.berd.dev.dtos;

import java.util.List;

public record RecapResponseDto(
        String periode,
        List<RecapPointDto> depenseMoyenneParPeriode,
        Double moyenneGlobale,
        String maxLabel,
        Double maxValue) {
}
