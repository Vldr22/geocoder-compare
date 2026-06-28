package io.github.vldr2212.geocodercompare.service;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.model.Coordinates;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.entity.ProviderGeocoding;
import io.github.vldr2212.geocodercompare.repository.GeocodingComparisonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Сборка и сохранение результата сравнения геокодеров в БД.
 */
@Service
@RequiredArgsConstructor
public class ComparisonStorageService {

    private final GeocodingComparisonRepository repository;

    public GeocodingComparison save(String address, GeocodePair results, BigDecimal distanceMeters) {
        GeocodingComparison comparison = new GeocodingComparison();

        comparison.setAddress(address);
        comparison.setYandex(toProviderGeocoding(results.yandex()));
        comparison.setDadata(toProviderGeocoding(results.dadata()));
        comparison.setDistanceMeters(distanceMeters);

        return repository.save(comparison);
    }

    /**
     * Преобразует результат геокодера в данные провайдера для записи.
     * Возвращает {@code null}, если источник не нашёл адрес - провайдер в записи остаётся пустым.
     */
    private ProviderGeocoding toProviderGeocoding(GeocodeResult result) {
        if (result == null) {
            return null;
        }

        Coordinates coordinates = result.coordinates();
        return new ProviderGeocoding(coordinates.latitude(), coordinates.longitude(), result.precision());
    }
}
