package io.github.vldr2212.geocodercompare.client;

import io.github.vldr2212.geocodercompare.model.Coordinates;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;

public record GeocodeResult(
        Coordinates coordinates,
        GeocodePrecision precision
) {
}