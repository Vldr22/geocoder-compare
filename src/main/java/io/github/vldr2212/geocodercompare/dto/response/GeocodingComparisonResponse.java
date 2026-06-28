package io.github.vldr2212.geocodercompare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeocodingComparisonResponse(
        UUID requestId,
        String address,
        ProviderResult yandex,
        ProviderResult dadata,
        BigDecimal distanceMeters,
        boolean reliable,
        Instant createdAt
) {
}