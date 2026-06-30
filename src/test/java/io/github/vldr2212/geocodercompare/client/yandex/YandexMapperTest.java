package io.github.vldr2212.geocodercompare.client.yandex;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("YandexMapper - маппинг ответа Yandex в доменный результат")
class YandexMapperTest {

    private final YandexMapper mapper = new YandexMapper();

    @Test
    @DisplayName("toGeocodeResult - разворачивает перевёрнутый pos в широту и долготу")
    void toGeocodeResult_shouldParseInvertedPos() {
        // given
        YandexResponse.GeoObject geoObject = geoObject("37.617300 55.757980", "exact");

        // when
        GeocodeResult result = mapper.toGeocodeResult(geoObject);

        // then
        assertThat(result.coordinates().latitude()).isEqualByComparingTo("55.757980");
        assertThat(result.coordinates().longitude()).isEqualByComparingTo("37.617300");
    }

    @ParameterizedTest
    @CsvSource({
            "exact, HOUSE",
            "number, HOUSE",
            "near, HOUSE",
            "range, STREET",
            "street, STREET",
            "other, LOCALITY",
            "unknown, NONE"
    })
    @DisplayName("toGeocodeResult - переводит precision в уровень точности")
    void toGeocodeResult_shouldMapPrecision_fromYandexPrecision(String yandexPrecision, GeocodePrecision expected) {
        // given
        YandexResponse.GeoObject geoObject = geoObject("37.61 55.75", yandexPrecision);

        // when
        GeocodeResult result = mapper.toGeocodeResult(geoObject);

        // then
        assertThat(result.precision()).isEqualTo(expected);
    }

    @Test
    @DisplayName("toGeocodeResult - возвращает NONE когда координат нет")
    void toGeocodeResult_shouldReturnNone_whenCoordinatesMissing() {
        // given
        YandexResponse.GeoObject geoObject = new YandexResponse.GeoObject(
                null,
                new YandexResponse.MetaDataProperty(new YandexResponse.GeocoderMetaData("exact")));

        // when
        GeocodeResult result = mapper.toGeocodeResult(geoObject);

        // then
        assertThat(result.coordinates()).isNull();
        assertThat(result.precision()).isEqualTo(GeocodePrecision.NONE);
    }

    private YandexResponse.GeoObject geoObject(String pos, String precision) {
        return new YandexResponse.GeoObject(
                new YandexResponse.Point(pos),
                new YandexResponse.MetaDataProperty(new YandexResponse.GeocoderMetaData(precision)));
    }
}
