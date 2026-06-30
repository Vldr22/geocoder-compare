package io.github.vldr2212.geocodercompare.dto.response;

import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Координаты и точность одного геокодера")
public record ProviderResult(

        @Schema(description = "Широта", example = "55.878256")
        BigDecimal latitude,

        @Schema(description = "Долгота", example = "37.653720")
        BigDecimal longitude,

        @Schema(description = "Уровень точности", example = "HOUSE")
        GeocodePrecision precision
) {
}