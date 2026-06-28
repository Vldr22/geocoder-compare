package io.github.vldr2212.geocodercompare.util;

import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.entity.ProviderGeocoding;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.properties.ComparisonProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Оценка надёжности сравнения: результат надёжен, когда оба источника определили адрес
 * с точностью до дома и сошлись в пределах настраиваемого порога расстояния.
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(ComparisonProperties.class)
public class ReliabilityEvaluator {

    private final ComparisonProperties properties;

    public boolean isReliable(GeocodingComparison comparison) {
        return isHouseLevel(comparison.getYandex())
                && isHouseLevel(comparison.getDadata())
                && isDistanceWithinThreshold(comparison.getDistanceMeters());
    }

    private boolean isHouseLevel(ProviderGeocoding geocoding) {
        return geocoding != null && geocoding.getPrecision() == GeocodePrecision.HOUSE;
    }

    private boolean isDistanceWithinThreshold(BigDecimal distanceMeters) {
        return distanceMeters != null
                && distanceMeters.compareTo(BigDecimal.valueOf(properties.reliableThresholdMeters())) <= 0;
    }
}
