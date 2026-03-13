package com.berd.dev.dtos;

import java.util.List;

public record CategorieSerieDto(
        String name,
        List<Double> values) {
}
