package io.github.vldr2212.geocodercompare.mapper;

import io.github.vldr2212.geocodercompare.dto.response.GeocodingComparisonResponse;
import io.github.vldr2212.geocodercompare.dto.response.ProviderResult;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.entity.ProviderGeocoding;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import org.springframework.stereotype.Component;

/**
 * Маппинг сущности сравнения в ответ API.
 */
@Component
public class GeocodingComparisonMapper {

    public GeocodingComparisonResponse toGeocodingResponse(GeocodingComparison comparison) {
        return new GeocodingComparisonResponse(
                comparison.getPublicId(),
                comparison.getAddress(),
                toProviderResult(comparison.getYandex()),
                toProviderResult(comparison.getDadata()),
                comparison.getDistanceMeters(),
                reliable(comparison),
                comparison.getCreatedAt()
        );
    }

    private ProviderResult toProviderResult(ProviderGeocoding geocoding) {
        if (geocoding == null) {
            return null;
        }

        return new ProviderResult(geocoding.getLatitude(), geocoding.getLongitude(), geocoding.getPrecision());
    }

    /**
     * Результат надёжен, когда оба источника определили адрес с точностью до дома (HOME).
     */
    private boolean reliable(GeocodingComparison comparison) {
        return isHouseLevel(comparison.getYandex()) && isHouseLevel(comparison.getDadata());
    }

    private boolean isHouseLevel(ProviderGeocoding geocoding) {
        return geocoding != null && geocoding.getPrecision() == GeocodePrecision.HOUSE;
    }
}