package com.berd.dev.dtos;

import java.util.List;

public record CategorieCourbeResponseDto(
        String periode,
        List<String> labels,
        List<CategorieSerieDto> series) {
}
