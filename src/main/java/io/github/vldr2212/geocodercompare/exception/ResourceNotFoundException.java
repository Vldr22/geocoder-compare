package io.github.vldr2212.geocodercompare.exception;

import io.github.vldr2212.geocodercompare.constants.ErrorMessages;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String identifier;

    private ResourceNotFoundException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public static ResourceNotFoundException byPublicId(UUID publicId) {
        return new ResourceNotFoundException(
                String.format("%s%s", ErrorMessages.COMPARISON_NOT_FOUND, publicId),
                publicId.toString());
    }
}