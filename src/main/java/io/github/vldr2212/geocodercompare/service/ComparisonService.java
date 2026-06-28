package io.github.vldr2212.geocodercompare.service;

import io.github.vldr2212.geocodercompare.exception.AddressNotFoundException;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сравнение геокодеров Yandex и Dadata по текстовому адресу.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComparisonService {

    private final ParallelGeocoder parallelGeocoder;
    private final ComparisonStorageService storageService;
    private final DistanceCalculator distanceCalculator;

    /**
     * Геокодирует адрес обоими источниками параллельно, считает расхождение и сохраняет результат.
     * Запись сохраняется и когда адрес найден не всеми источниками - для аудита; в этом случае
     * после сохранения бросается {@link AddressNotFoundException}.
     *
     * @throws AddressNotFoundException если хотя бы один источник не нашёл адрес
     */
    public GeocodingComparison compare(String address) {
        log.debug("Comparing geocoders for address: {}", address);

        GeocodePair results = parallelGeocoder.geocode(address);

        if (!results.anyFound()) {
            throw AddressNotFoundException.forAddress(address);
        }

        boolean bothFound = results.bothFound();
        BigDecimal distanceMeters = bothFound ? distance(results) : null;
        GeocodingComparison comparison = storageService.save(address, results, distanceMeters);

        log.debug("Comparison saved: publicId - {}, distanceMeters - {}",
                comparison.getPublicId(), comparison.getDistanceMeters());

        if (!bothFound) {
            throw AddressNotFoundException.forAddress(address);
        }

        return comparison;
    }

    /**
     * Возвращает сохранённое сравнение по публичному идентификатору.
     */
    public GeocodingComparison getByPublicId(UUID publicId) {
        return storageService.getByPublicId(publicId);
    }

    private BigDecimal distance(GeocodePair results) {
        return distanceCalculator.calculateDistance(
                results.yandex().coordinates(), results.dadata().coordinates());
    }
}
