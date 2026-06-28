package io.github.vldr2212.geocodercompare.service;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;

/**
 * Результаты геокодирования адреса обоими источниками.
 */
public record GeocodePair(
        GeocodeResult yandex,
        GeocodeResult dadata) {

    public boolean bothFound() {
        return yandex != null && dadata != null;
    }
}