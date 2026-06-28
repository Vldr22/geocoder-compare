package io.github.vldr2212.geocodercompare.dto.request;

import io.github.vldr2212.geocodercompare.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = ValidationMessages.ADDRESS_NOT_BLANK)
        @Size(max = 500, message = ValidationMessages.ADDRESS_TOO_LONG)
        String address
) {
}