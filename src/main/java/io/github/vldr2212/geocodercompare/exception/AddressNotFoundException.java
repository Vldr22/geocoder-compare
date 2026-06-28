package io.github.vldr2212.geocodercompare.exception;

import io.github.vldr2212.geocodercompare.constants.ErrorMessages;
import lombok.Getter;

@Getter
public class AddressNotFoundException extends RuntimeException {

    private final String address;

    private AddressNotFoundException(String message, String address) {
        super(message);
        this.address = address;
    }

    public static AddressNotFoundException forAddress(String address) {
        return new AddressNotFoundException(
                String.format("%s%s", ErrorMessages.ADDRESS_NOT_FOUND, address),
                address);
    }
}
