package io.github.vldr2212.geocodercompare.constants;

import lombok.experimental.UtilityClass;

/**
 * Тексты сообщений валидации входящих данных.
 */
@UtilityClass
public class ValidationMessages {

    public final String ADDRESS_NOT_BLANK = "Адрес не может быть пустым";
    public final String ADDRESS_TOO_LONG = "Адрес не может быть длиннее 500 символов";
}