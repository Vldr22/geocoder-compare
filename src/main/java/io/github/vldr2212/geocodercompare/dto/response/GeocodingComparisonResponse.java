package io.github.vldr2212.geocodercompare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Результат сравнения геокодеров")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeocodingComparisonResponse(

        @Schema(description = "Идентификатор сравнения", example = "731f26b1-00ca-4c57-b724-a4f239e1d66d")
        UUID requestId,

        @Schema(description = "Запрошенный адрес", example = "Москва Сухонская 11")
        String address,

        @Schema(description = "Результат Yandex; отсутствует, если источник не нашёл адрес")
        ProviderResult yandex,

        @Schema(description = "Результат Dadata; отсутствует, если источник не нашёл адрес")
        ProviderResult dadata,

        @Schema(description = "Расхождение между точками в метрах; отсутствует, если не вычислимо", example = "47.21")
        BigDecimal distanceMeters,

        @Schema(description = "Надёжность: обе точки до дома и сошлись в пределах порога", example = "true")
        boolean reliable,

        @Schema(description = "Время создания в UTC", example = "2026-06-28T13:47:43Z")
        Instant createdAt
) {
}