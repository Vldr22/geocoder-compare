package io.github.vldr2212.geocodercompare.exception;

import io.github.vldr2212.geocodercompare.constants.ErrorMessages;
import io.github.vldr2212.geocodercompare.model.enums.GeocoderProvider;
import lombok.Getter;

@Getter
public class GeocoderUnavailableException extends RuntimeException {

    private final GeocoderProvider provider;

    public GeocoderUnavailableException(GeocoderProvider provider, Throwable cause) {
        super(String.format("%s%s", ErrorMessages.GEOCODER_UNAVAILABLE, provider), cause);
        this.provider = provider;
    }
}