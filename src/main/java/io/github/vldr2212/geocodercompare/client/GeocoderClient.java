package io.github.vldr2212.geocodercompare.client;

import io.github.vldr2212.geocodercompare.model.enums.GeocoderProvider;

import java.util.Optional;

public interface GeocoderClient {

    GeocoderProvider provider();

    Optional<GeocodeResult> geocode(String address);

}