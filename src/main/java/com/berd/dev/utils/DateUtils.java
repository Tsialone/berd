package com.berd.dev.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static boolean isConflit(LocalDateTime a, LocalDateTime b, LocalDateTime cible) {
        if (a == null || b == null || cible == null) {
            return false;
        }

        return !cible.isBefore(a) && !cible.isAfter(b);
    }

    public static boolean isConflit(LocalDateTime a, LocalDateTime b, LocalDateTime c, LocalDateTime d) {
        if (a == null || b == null || c == null || d == null) {
            return false;
        }

        return !b.isBefore(c) && !a.isAfter(d);
    }

    public static long differenceEnMinutes(LocalDateTime debut, LocalDateTime fin) {
        return ChronoUnit.MINUTES.between(debut, fin);
    }

}
