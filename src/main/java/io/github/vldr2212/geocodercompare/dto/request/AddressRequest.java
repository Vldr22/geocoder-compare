package io.github.vldr2212.geocodercompare.dto.request;

import io.github.vldr2212.geocodercompare.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на сравнение геокодеров по адресу")
public record AddressRequest(

        @Schema(description = "Текстовый адрес", example = "Москва Сухонская 11")
        @NotBlank(message = ValidationMessages.ADDRESS_NOT_BLANK)
        @Size(max = 500, message = ValidationMessages.ADDRESS_TOO_LONG)
        String address
) {
}