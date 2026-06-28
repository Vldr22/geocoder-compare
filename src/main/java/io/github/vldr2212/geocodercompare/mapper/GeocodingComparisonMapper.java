package io.github.vldr2212.geocodercompare.mapper;

import io.github.vldr2212.geocodercompare.dto.response.GeocodingComparisonResponse;
import io.github.vldr2212.geocodercompare.dto.response.ProviderResult;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.entity.ProviderGeocoding;
import io.github.vldr2212.geocodercompare.util.ReliabilityEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Маппинг сущности сравнения в ответ API.
 */
@Component
@RequiredArgsConstructor
public class GeocodingComparisonMapper {

    private final ReliabilityEvaluator reliabilityEvaluator;

    public GeocodingComparisonResponse toGeocodingResponse(GeocodingComparison comparison) {
        boolean reliable = reliabilityEvaluator.isReliable(comparison);

        return new GeocodingComparisonResponse(
                comparison.getPublicId(),
                comparison.getAddress(),
                toProviderResult(comparison.getYandex()),
                toProviderResult(comparison.getDadata()),
                comparison.getDistanceMeters(),
                reliable,
                comparison.getCreatedAt()
        );
    }

    private ProviderResult toProviderResult(ProviderGeocoding geocoding) {
        if (geocoding == null) {
            return null;
        }

        return new ProviderResult(geocoding.getLatitude(), geocoding.getLongitude(), geocoding.getPrecision());
    }
}