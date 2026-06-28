package io.github.vldr2212.geocodercompare.util;

import io.github.vldr2212.geocodercompare.model.Coordinates;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Расчёт расстояния между двумя точками по формуле Haversine.
 * Сферическая модель Земли (погрешность ~0.5%) достаточна для оценки расхождения
 * геокодеров; результат округляется до сотых метра.
 */
@Component
public class DistanceCalculator {

    private static final double EARTH_RADIUS_METERS = 6_371_000;
    private static final int RESULT_SCALE = 2;

    public BigDecimal calculateDistance(Coordinates from, Coordinates to) {
        double fromLat = from.latitude().doubleValue();
        double fromLon = from.longitude().doubleValue();

        double toLat = to.latitude().doubleValue();
        double toLon = to.longitude().doubleValue();

        double meters = distanceMeters(fromLat, fromLon, toLat, toLon);

        return BigDecimal.valueOf(meters).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }

    private double distanceMeters(double fromLat, double fromLon, double toLat, double toLon) {
        double deltaLat = Math.toRadians(toLat - fromLat);
        double deltaLon = Math.toRadians(toLon - fromLon);

        double fromLatRad = Math.toRadians(fromLat);
        double toLatRad = Math.toRadians(toLat);

        double sinLat = Math.sin(deltaLat / 2);
        double sinLon = Math.sin(deltaLon / 2);

        double a = sinLat * sinLat
                + Math.cos(fromLatRad) * Math.cos(toLatRad) * sinLon * sinLon;

        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS_METERS * c;
    }
}