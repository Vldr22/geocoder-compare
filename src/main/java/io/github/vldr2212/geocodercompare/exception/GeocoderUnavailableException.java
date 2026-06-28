package io.github.vldr2212.geocodercompare.exception;

import io.github.vldr2212.geocodercompare.constants.ErrorMessages;
import io.github.vldr2212.geocodercompare.model.enums.GeocoderProvider;
import lombok.Getter;

@Getter
public class GeocoderUnavailableException extends RuntimeException {

    private final GeocoderProvider provider;

    private GeocoderUnavailableException(String message, GeocoderProvider provider, Throwable cause) {
        super(message, cause);
        this.provider = provider;
    }

    public static GeocoderUnavailableException forProvider(GeocoderProvider provider, Throwable cause) {
        return new GeocoderUnavailableException(
                String.format("%s%s", ErrorMessages.GEOCODER_UNAVAILABLE, provider),
                provider,
                cause);
    }
}