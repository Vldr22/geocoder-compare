package io.github.vldr2212.geocodercompare.constants;

import lombok.experimental.UtilityClass;

/**
 * Тексты ошибок API.
 */
@UtilityClass
public class ErrorMessages {

    // ========== GEOCODER ==========
    public final String GEOCODER_UNAVAILABLE = "Геокодер недоступен: ";
    public final String ADDRESS_NOT_FOUND = "Адрес не найден хотя бы одним геокодером: ";
    public final String COMPARISON_NOT_FOUND = "Сравнение не найдено по идентификатору: ";

    // ========== REQUEST ==========
    public final String VALIDATION_FAILED = "Ошибка валидации запроса";
    public final String INVALID_REQUEST_BODY = "Некорректное тело запроса";
    public final String INVALID_PARAMETER_TYPE = "Некорректный формат параметра";
    public final String METHOD_NOT_ALLOWED = "Метод не поддерживается";
    public final String INTERNAL_ERROR = "Внутренняя ошибка сервера";
}