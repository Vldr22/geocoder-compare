package io.github.vldr2212.geocodercompare.client.yandex;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.model.Coordinates;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class YandexMapper {

    private static final String POS_DELIMITER = " ";
    private static final int LONGITUDE_INDEX = 0;
    private static final int LATITUDE_INDEX = 1;

    public GeocodeResult toGeocodeResult(YandexResponse.GeoObject geoObject) {
        return new GeocodeResult(toCoordinates(geoObject), mapPrecision(geoObject));
    }

    private Coordinates toCoordinates(YandexResponse.GeoObject geoObject) {
        String pos = extractPos(geoObject);

        if (pos == null) {
            return null;
        }

        String[] parts = pos.split(POS_DELIMITER);

        BigDecimal longitude = new BigDecimal(parts[LONGITUDE_INDEX]);
        BigDecimal latitude = new BigDecimal(parts[LATITUDE_INDEX]);

        return new Coordinates(latitude, longitude);
    }

    /**
     * Маппинг уровня точности Yandex (yg:precision) в доменный {@link GeocodePrecision}:
     * exact/number/near - дом, range/street - улица, other - населённый пункт, иначе - не определено.
     */
    private GeocodePrecision mapPrecision(YandexResponse.GeoObject geoObject) {
        String precision = extractPrecision(geoObject);

        if (precision == null) {
            return GeocodePrecision.NONE;
        }

        return switch (precision) {
            case "exact", "number", "near" -> GeocodePrecision.HOUSE;
            case "range", "street" -> GeocodePrecision.STREET;
            case "other" -> GeocodePrecision.LOCALITY;
            default -> GeocodePrecision.NONE;
        };
    }

    private String extractPos(YandexResponse.GeoObject geoObject) {
        return geoObject.point() == null ? null : geoObject.point().pos();
    }

    private String extractPrecision(YandexResponse.GeoObject geoObject) {
        YandexResponse.MetaDataProperty meta = geoObject.metaDataProperty();

        if (meta == null || meta.geocoderMetaData() == null) {
            return null;
        }

        return meta.geocoderMetaData().precision();
    }
}
