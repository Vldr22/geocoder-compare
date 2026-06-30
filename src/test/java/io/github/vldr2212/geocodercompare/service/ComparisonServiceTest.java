package io.github.vldr2212.geocodercompare.service;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.exception.AddressNotFoundException;
import io.github.vldr2212.geocodercompare.model.Coordinates;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.util.DistanceCalculator;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComparisonService - сравнение геокодеров Yandex и Dadata по текстовому адресу")
public class ComparisonServiceTest {

    @Mock
    private ParallelGeocoder parallelGeocoder;

    @Mock
    private ComparisonStorageService comparisonStorageService;

    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private ComparisonService comparisonService;

    private final Faker faker = new Faker();

    private GeocodeResult yandex;
    private GeocodeResult dadata;
    private String address;
    private BigDecimal distance;

    @BeforeEach
    void setUp() {
        address = faker.address().fullAddress();
        distance = BigDecimal.valueOf(100.00);
        yandex = new GeocodeResult(new Coordinates(BigDecimal.ONE, BigDecimal.ONE), GeocodePrecision.HOUSE);
        dadata = new GeocodeResult(new Coordinates(BigDecimal.ONE, BigDecimal.ONE), GeocodePrecision.STREET);
    }

    @Test
    @DisplayName("compare - возвращает сравнение когда оба провайдера вернули данные")
    void compare_shouldReturnComparison_whenBothProvidersFound() {
        // given
        GeocodePair geocodePair = new GeocodePair(yandex, dadata);
        GeocodingComparison comparison = new GeocodingComparison();

        when(parallelGeocoder.geocode(address)).thenReturn(geocodePair);
        when(distanceCalculator.calculateDistance(yandex.coordinates(), dadata.coordinates())).thenReturn(distance);
        when(comparisonStorageService.save(eq(address), eq(geocodePair), eq(distance))).thenReturn(comparison);

        // when
        GeocodingComparison result = comparisonService.compare(address);

        // then
        assertThat(result).isEqualTo(comparison);

        verify(parallelGeocoder).geocode(address);
        verify(distanceCalculator).calculateDistance(yandex.coordinates(), dadata.coordinates());
        verify(comparisonStorageService).save(address, geocodePair, distance);
    }


    @Test
    @DisplayName("compare - сохраняет результат и бросает ошибку когда данные вернул только один провайдер")
    void compare_shouldSaveAndThrow_whenOnlyOneProviderFound() {
        // given
        GeocodePair geocodePair = new GeocodePair(yandex, null);
        GeocodingComparison comparison = new GeocodingComparison();

        when(parallelGeocoder.geocode(address)).thenReturn(geocodePair);
        when(comparisonStorageService.save(eq(address), eq(geocodePair), isNull())).thenReturn(comparison);

        //when
        assertThatThrownBy(() -> comparisonService.compare(address))
                .isInstanceOf(AddressNotFoundException.class);

        // then
        verify(parallelGeocoder).geocode(address);
        verify(distanceCalculator, never()).calculateDistance(any(), any());
        verify(comparisonStorageService).save(eq(address), eq(geocodePair), isNull());
        verifyNoMoreInteractions(parallelGeocoder, comparisonStorageService);
    }


    @Test
    @DisplayName("compare - не сохраняет и бросает ошибку когда оба провайдера не вернули данных")
    void compare_shouldNotSaveAndThrow_whenNoProviderFound() {
        // given
        GeocodePair geocodePair = new GeocodePair(null, null);

        when(parallelGeocoder.geocode(address)).thenReturn(geocodePair);

        // when
        assertThatThrownBy(() -> comparisonService.compare(address))
                .isInstanceOf(AddressNotFoundException.class);

        // then
        verify(parallelGeocoder).geocode(address);
        verify(distanceCalculator, never()).calculateDistance(any(), any());
        verify(comparisonStorageService, never()).save(any(), any(), any());
    }

}
