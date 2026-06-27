package io.github.vldr2212.geocodercompare.client.dadata;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.model.Coordinates;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DadataMapper {

    public GeocodeResult toGeocodeResult(DadataGeoResult address) {
        return new GeocodeResult(toCoordinates(address), mapPrecision(address.qcGeo()));
    }

    private Coordinates toCoordinates(DadataGeoResult address) {
        if (address.geoLat() == null || address.geoLon() == null) {
            return null;
        }

        return new Coordinates(
                new BigDecimal(address.geoLat()),
                new BigDecimal(address.geoLon())
        );
    }

    /**
     * Маппинг кода качества DaData (qc_geo) в доменный {@link GeocodePrecision}:
     * 0,1 - дом, 2 - улица, 3,4 - населённый пункт, 5/прочее - не определено.
     */
    private GeocodePrecision mapPrecision(Integer qcGeo) {
        if (qcGeo == null) {
            return GeocodePrecision.NONE;
        }

        return switch (qcGeo) {
            case 0, 1 -> GeocodePrecision.HOUSE;
            case 2 -> GeocodePrecision.STREET;
            case 3, 4 -> GeocodePrecision.LOCALITY;
            default -> GeocodePrecision.NONE;
        };
    }
}