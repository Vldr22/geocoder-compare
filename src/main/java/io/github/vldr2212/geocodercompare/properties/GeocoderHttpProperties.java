package io.github.vldr2212.geocodercompare.properties;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "geocoder.http")
public record GeocoderHttpProperties(
        @NotNull
        @DurationMin(millis = 100)
        @DurationMax(seconds = 10)
        Duration connectTimeout,

        @NotNull
        @DurationMin(millis = 100)
        @DurationMax(seconds = 10)
        Duration responseTimeout

) {
}