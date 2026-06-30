package io.github.vldr2212.geocodercompare.client.dadata;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DadataMapper - маппинг ответа Dadata в доменный результат")
class DadataMapperTest {

    private final DadataMapper mapper = new DadataMapper();

    @Test
    @DisplayName("toGeocodeResult - разбирает координаты из строк ответа")
    void toGeocodeResult_shouldParseCoordinates_whenResultValid() {
        // given
        DadataGeoResult input = new DadataGeoResult("55.757980", "37.617300", 0);

        // when
        GeocodeResult result = mapper.toGeocodeResult(input);

        // then
        assertThat(result.coordinates().latitude()).isEqualByComparingTo("55.757980");
        assertThat(result.coordinates().longitude()).isEqualByComparingTo("37.617300");
    }

    @ParameterizedTest
    @CsvSource({
            "0, HOUSE",
            "1, HOUSE",
            "2, STREET",
            "3, LOCALITY",
            "4, LOCALITY",
            "5, NONE"
    })
    @DisplayName("toGeocodeResult - переводит qc_geo в уровень точности")
    void toGeocodeResult_shouldMapPrecision_fromQcGeo(int qcGeo, GeocodePrecision expected) {
        // given
        DadataGeoResult input = new DadataGeoResult("55.75", "37.61", qcGeo);

        // when
        GeocodeResult result = mapper.toGeocodeResult(input);

        // then
        assertThat(result.precision()).isEqualTo(expected);
    }

    @Test
    @DisplayName("toGeocodeResult - возвращает NONE когда координат нет")
    void toGeocodeResult_shouldReturnNone_whenCoordinatesMissing() {
        // given
        DadataGeoResult input = new DadataGeoResult(null, null, 0);

        // when
        GeocodeResult result = mapper.toGeocodeResult(input);

        // then
        assertThat(result.coordinates()).isNull();
        assertThat(result.precision()).isEqualTo(GeocodePrecision.NONE);
    }
}
