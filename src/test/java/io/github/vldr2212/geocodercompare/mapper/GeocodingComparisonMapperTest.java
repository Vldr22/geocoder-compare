package io.github.vldr2212.geocodercompare.mapper;

import io.github.vldr2212.geocodercompare.dto.response.GeocodingComparisonResponse;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.entity.ProviderGeocoding;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.util.ReliabilityEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GeocodingComparisonMapper - маппинг сущности в ответ API")
class GeocodingComparisonMapperTest {

    @Mock
    private ReliabilityEvaluator reliabilityEvaluator;

    @InjectMocks
    private GeocodingComparisonMapper mapper;

    private GeocodingComparison comparison;

    @BeforeEach
    void setUp() {
        comparison = new GeocodingComparison();
        comparison.setPublicId(UUID.randomUUID());
        comparison.setAddress("Москва Сухонская 11");
        comparison.setYandex(new ProviderGeocoding(BigDecimal.ONE, BigDecimal.ONE, GeocodePrecision.HOUSE));
        comparison.setDadata(new ProviderGeocoding(BigDecimal.TEN, BigDecimal.TEN, GeocodePrecision.STREET));
        comparison.setDistanceMeters(BigDecimal.valueOf(100));
        comparison.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("toGeocodingResponse - переносит все поля сущности в ответ")
    void toGeocodingResponse_shouldMapAllFields() {
        // given
        when(reliabilityEvaluator.isReliable(comparison)).thenReturn(true);

        // when
        GeocodingComparisonResponse response = mapper.toGeocodingResponse(comparison);

        // then
        assertThat(response.requestId()).isEqualTo(comparison.getPublicId());
        assertThat(response.address()).isEqualTo(comparison.getAddress());
        assertThat(response.distanceMeters()).isEqualByComparingTo(comparison.getDistanceMeters());
        assertThat(response.createdAt()).isEqualTo(comparison.getCreatedAt());
        assertThat(response.yandex().precision()).isEqualTo(GeocodePrecision.HOUSE);
        assertThat(response.dadata().precision()).isEqualTo(GeocodePrecision.STREET);
        assertThat(response.reliable()).isTrue();
    }

    @Test
    @DisplayName("toGeocodingResponse - провайдер без данных маппится в null")
    void toGeocodingResponse_shouldMapNullProvider_whenMissing() {
        // given
        comparison.setDadata(null);

        // when
        GeocodingComparisonResponse response = mapper.toGeocodingResponse(comparison);

        // then
        assertThat(response.yandex()).isNotNull();
        assertThat(response.dadata()).isNull();
    }
}
