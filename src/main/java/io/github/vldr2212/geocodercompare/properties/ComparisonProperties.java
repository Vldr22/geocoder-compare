package io.github.vldr2212.geocodercompare.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Параметры оценки сравнения геокодеров.
 */
@Validated
@ConfigurationProperties(prefix = "geocoder.comparison")
public record ComparisonProperties(
        @Positive
        int reliableThresholdMeters
) {
}