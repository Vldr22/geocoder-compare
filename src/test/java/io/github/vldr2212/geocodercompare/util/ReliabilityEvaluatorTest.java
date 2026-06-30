package io.github.vldr2212.geocodercompare.util;

import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.entity.ProviderGeocoding;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.properties.ComparisonProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReliabilityEvaluator - оценка надёжности сравнения")
class ReliabilityEvaluatorTest {

    private static final int THRESHOLD_METERS = 100;

    private final ReliabilityEvaluator evaluator = new ReliabilityEvaluator(new ComparisonProperties(THRESHOLD_METERS));

    @Test
    @DisplayName("isReliable - true когда обе точки HOUSE и расстояние в пределах порога")
    void isReliable_shouldReturnTrue_whenBothHouseAndWithinThreshold() {
        // given
        GeocodingComparison comparison =
                comparison(GeocodePrecision.HOUSE, GeocodePrecision.HOUSE, BigDecimal.valueOf(50));

        // when
        boolean result = evaluator.isReliable(comparison);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isReliable - true когда расстояние ровно на пороге")
    void isReliable_shouldReturnTrue_whenDistanceEqualsThreshold() {
        // given
        GeocodingComparison comparison =
                comparison(GeocodePrecision.HOUSE, GeocodePrecision.HOUSE, BigDecimal.valueOf(THRESHOLD_METERS));

        // when
        boolean result = evaluator.isReliable(comparison);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isReliable - false когда обе HOUSE, но расстояние больше порога")
    void isReliable_shouldReturnFalse_whenDistanceExceedsThreshold() {
        // given
        GeocodingComparison comparison =
                comparison(GeocodePrecision.HOUSE, GeocodePrecision.HOUSE, BigDecimal.valueOf(8000));

        // when
        boolean result = evaluator.isReliable(comparison);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isReliable - false когда один провайдер определил адрес только до улицы")
    void isReliable_shouldReturnFalse_whenOneProviderIsStreetLevel() {
        // given
        GeocodingComparison comparison =
                comparison(GeocodePrecision.HOUSE, GeocodePrecision.STREET, BigDecimal.valueOf(28));

        // when
        boolean result = evaluator.isReliable(comparison);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isReliable - false когда провайдер не нашёл адрес")
    void isReliable_shouldReturnFalse_whenProviderMissing() {
        // given
        GeocodingComparison comparison = new GeocodingComparison();
        comparison.setYandex(providerGeocoding(GeocodePrecision.HOUSE));
        comparison.setDadata(null);
        comparison.setDistanceMeters(null);

        // when
        boolean result = evaluator.isReliable(comparison);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isReliable - false когда расстояние не вычислено")
    void isReliable_shouldReturnFalse_whenDistanceIsNull() {
        // given
        GeocodingComparison comparison =
                comparison(GeocodePrecision.HOUSE, GeocodePrecision.HOUSE, null);

        // when
        boolean result = evaluator.isReliable(comparison);

        // then
        assertThat(result).isFalse();
    }

    private GeocodingComparison comparison(GeocodePrecision yandex,
                                           GeocodePrecision dadata,
                                           BigDecimal distanceMeters) {
        GeocodingComparison comparison = new GeocodingComparison();

        comparison.setYandex(providerGeocoding(yandex));
        comparison.setDadata(providerGeocoding(dadata));
        comparison.setDistanceMeters(distanceMeters);
        return comparison;
    }

    private ProviderGeocoding providerGeocoding(GeocodePrecision precision) {
        return new ProviderGeocoding(null, null, precision);
    }
}
