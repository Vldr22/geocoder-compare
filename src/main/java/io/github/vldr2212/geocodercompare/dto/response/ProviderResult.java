package io.github.vldr2212.geocodercompare.dto.response;

import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;

import java.math.BigDecimal;

public record ProviderResult(
        BigDecimal latitude,
        BigDecimal longitude,
        GeocodePrecision precision
) {
}