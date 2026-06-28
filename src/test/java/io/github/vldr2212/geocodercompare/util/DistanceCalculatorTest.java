package io.github.vldr2212.geocodercompare.util;

import io.github.vldr2212.geocodercompare.model.Coordinates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("DistanceCalculator - расчёт расстояния по Haversine")
class DistanceCalculatorTest {

    private final DistanceCalculator calculator = new DistanceCalculator();

    @Test
    @DisplayName("calculateDistance - возвращает 0 для совпадающих точек")
    void calculateDistance_shouldReturnZero_whenSamePoint() {
        // given
        Coordinates point = coordinates(55.7558, 37.6173);

        // when
        BigDecimal result = calculator.calculateDistance(point, point);

        // then
        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateDistance - эталон Москва-Санкт-Петербург ~ 634 км")
    void calculateDistance_shouldMatchReference_whenMoscowToSaintPetersburg() {
        // given
        Coordinates moscow = coordinates(55.7558, 37.6173);
        Coordinates saintPetersburg = coordinates(59.9343, 30.3351);

        // when
        BigDecimal result = calculator.calculateDistance(moscow, saintPetersburg);

        // then - погрешность ~0.5% сферической модели, допуск 2 км
        assertThat(result.doubleValue()).isCloseTo(634_000, within(2_000.0));
    }

    @Test
    @DisplayName("calculateDistance - один градус долготы на экваторе ~ 111.3 км")
    void calculateDistance_shouldMatchReference_whenOneDegreeAtEquator() {
        // given
        Coordinates origin = coordinates(0.0, 0.0);
        Coordinates oneDegreeEast = coordinates(0.0, 1.0);

        // when
        BigDecimal result = calculator.calculateDistance(origin, oneDegreeEast);

        // then
        assertThat(result.doubleValue()).isCloseTo(111_195, within(500.0));
    }

    @Test
    @DisplayName("calculateDistance - результат округлён до целых метров")
    void calculateDistance_shouldReturnIntegerMeters() {
        // given
        Coordinates from = coordinates(55.7558, 37.6173);
        Coordinates to = coordinates(55.7600, 37.6200);

        // when
        BigDecimal result = calculator.calculateDistance(from, to);

        // then
        assertThat(result.scale()).isZero();
    }

    private Coordinates coordinates(double latitude, double longitude) {
        return new Coordinates(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
    }
}