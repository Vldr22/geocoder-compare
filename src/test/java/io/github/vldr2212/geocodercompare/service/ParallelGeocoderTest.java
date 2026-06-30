package io.github.vldr2212.geocodercompare.service;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.client.GeocoderClient;
import io.github.vldr2212.geocodercompare.exception.GeocoderUnavailableException;
import io.github.vldr2212.geocodercompare.model.Coordinates;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.model.enums.GeocoderProvider;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParallelGeocoder - параллельный вызов геокодеров")
public class ParallelGeocoderTest {

    @Mock
    private GeocoderClient yandex;
    @Mock
    private GeocoderClient dadata;

    private ParallelGeocoder parallelGeocoder;

    private final Faker faker = new Faker();
    private String address;
    private GeocodeResult yandexResult;

    @BeforeEach
    void setUp() {
        parallelGeocoder = new ParallelGeocoder(yandex, dadata, Runnable::run);
        address = faker.address().fullAddress();
        yandexResult = new GeocodeResult(new Coordinates(BigDecimal.ONE, BigDecimal.ONE), GeocodePrecision.HOUSE);

    }

    @Test
    @DisplayName("geocode - возвращает результаты обоих провайдеров")
    void geocode_shouldReturnResultsFromBothProviders() {
        // given
        when(yandex.geocode(address)).thenReturn(Optional.of(yandexResult));
        when(dadata.geocode(address)).thenReturn(Optional.empty());

        // when
        GeocodePair result = parallelGeocoder.geocode(address);

        // then
        assertThat(result.yandex()).isEqualTo(yandexResult);
        assertThat(result.dadata()).isNull();
        verify(yandex).geocode(address);
        verify(dadata).geocode(address);

    }

    @Test
    @DisplayName("geocode - бросает ошибку когда любой провайдер недоступен")
    void geocode_shouldThrow_whenAnyProviderFails() {
        // given
        when(yandex.geocode(address)).thenReturn(Optional.of(yandexResult));
        when(dadata.geocode(address)).thenThrow(GeocoderUnavailableException.forProvider(
                GeocoderProvider.DADATA,
                new RuntimeException()
        ));

        //when & then
        assertThatThrownBy(() -> parallelGeocoder.geocode(address))
                .isInstanceOf(GeocoderUnavailableException.class);

        verify(yandex).geocode(address);
        verify(dadata).geocode(address);

    }

}