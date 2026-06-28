package io.github.vldr2212.geocodercompare.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Coordinates(
        BigDecimal latitude,
        BigDecimal longitude
) {

    public static final int SCALE = 6;

    public Coordinates {
        latitude = latitude.setScale(SCALE, RoundingMode.HALF_UP);
        longitude = longitude.setScale(SCALE, RoundingMode.HALF_UP);
    }
}