package io.github.vldr2212.geocodercompare.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "geocoder.yandex")
public record YandexProperties(
        @NotBlank String baseUrl,
        @NotBlank String apiKey
) {
}