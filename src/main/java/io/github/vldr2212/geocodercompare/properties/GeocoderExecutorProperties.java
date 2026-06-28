package io.github.vldr2212.geocodercompare.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Параметры пула потоков для вызовов геокодеров.
 */
@Validated
@ConfigurationProperties(prefix = "geocoder.executor")
public record GeocoderExecutorProperties(

        @Min(1)
        int corePoolSize,

        @Min(1)
        int maxPoolSize,

        @Min(1)
        int queueCapacity,

        @Min(0)
        @NotNull
        Integer awaitTerminationSeconds
) {
}
