package io.github.vldr2212.geocodercompare.model;

import java.math.BigDecimal;

public record Coordinates(
        BigDecimal latitude,
        BigDecimal longitude
) {
}