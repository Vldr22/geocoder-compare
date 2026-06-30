package io.github.vldr2212.geocodercompare.service;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.exception.ResourceNotFoundException;
import io.github.vldr2212.geocodercompare.model.Coordinates;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.repository.GeocodingComparisonRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComparisonStorageService - сохранение и чтение сравнений")
class ComparisonStorageServiceTest {

    @Mock
    private GeocodingComparisonRepository geocodingComparisonRepository;

    @InjectMocks
    private ComparisonStorageService comparisonStorageService;

    private final Faker faker = new Faker();

    private String address;
    private BigDecimal distance;
    private GeocodeResult yandex;
    private GeocodeResult dadata;

    @BeforeEach
    void setUp() {
        address = faker.address().fullAddress();
        distance = BigDecimal.valueOf(100.00);
        yandex = new GeocodeResult(new Coordinates(BigDecimal.ONE, BigDecimal.ONE), GeocodePrecision.HOUSE);
        dadata = new GeocodeResult(new Coordinates(BigDecimal.ONE, BigDecimal.ONE), GeocodePrecision.STREET);
    }

    @Test
    @DisplayName("save - собирает сущность из результатов и сохраняет в БД")
    void save_shouldBuildAndPersistComparison() {
        // given
        GeocodePair results = new GeocodePair(yandex, dadata);
        when(geocodingComparisonRepository.save(any(GeocodingComparison.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        GeocodingComparison saved = comparisonStorageService.save(address, results, distance);

        // then
        assertThat(saved.getAddress()).isEqualTo(address);
        assertThat(saved.getYandex().getPrecision()).isEqualTo(GeocodePrecision.HOUSE);
        assertThat(saved.getDadata().getPrecision()).isEqualTo(GeocodePrecision.STREET);
        assertThat(saved.getDistanceMeters()).isEqualByComparingTo(distance);
        verify(geocodingComparisonRepository).save(any(GeocodingComparison.class));

    }

    @Test
    @DisplayName("save - сохраняет провайдера пустым когда он не нашёл адрес")
    void save_shouldStoreNullProvider_whenProviderNotFound() {
        // given
        GeocodePair results = new GeocodePair(yandex, null);

        when(geocodingComparisonRepository.save(any(GeocodingComparison.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        GeocodingComparison saved = comparisonStorageService.save(address, results, null);

        // then
        assertThat(saved.getYandex()).isNotNull();
        assertThat(saved.getDadata()).isNull();
        assertThat(saved.getDistanceMeters()).isNull();
    }

    @Test
    @DisplayName("getByPublicId - возвращает запись когда она существует")
    void getByPublicId_shouldReturnComparison_whenExists() {
        // given
        UUID publicId = UUID.randomUUID();
        GeocodingComparison comparison = new GeocodingComparison();
        when(geocodingComparisonRepository.findByPublicId(publicId)).thenReturn(Optional.of(comparison));

        // when
        GeocodingComparison result = comparisonStorageService.getByPublicId(publicId);

        // then
        assertThat(result).isSameAs(comparison);
    }

    @Test
    @DisplayName("getByPublicId - бросает ошибку когда записи нет")
    void getByPublicId_shouldThrow_whenNotExists() {
        // given
        UUID publicId = UUID.randomUUID();
        when(geocodingComparisonRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> comparisonStorageService.getByPublicId(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
