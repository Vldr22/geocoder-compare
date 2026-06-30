package io.github.vldr2212.geocodercompare.service;

import io.github.vldr2212.geocodercompare.client.Dadata;
import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.client.GeocoderClient;
import io.github.vldr2212.geocodercompare.client.Yandex;
import io.github.vldr2212.geocodercompare.config.ExecutorConfig;
import io.github.vldr2212.geocodercompare.exception.GeocoderUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Параллельный вызов геокодеров: время ответа - max из источников, а не сумма.
 * Сбой любого источника всплывает сразу (приоритет отказа над "не найдено").
 */
@Slf4j
@Component
public class ParallelGeocoder {

    private final GeocoderClient yandexClient;
    private final GeocoderClient dadataClient;
    private final Executor geocoderExecutor;

    public ParallelGeocoder(@Yandex GeocoderClient yandexClient,
                            @Dadata GeocoderClient dadataClient,
                            @Qualifier(ExecutorConfig.GEOCODER_EXECUTOR) Executor geocoderExecutor) {
        this.yandexClient = yandexClient;
        this.dadataClient = dadataClient;
        this.geocoderExecutor = geocoderExecutor;
    }

    /**
     * Геокодирует адрес обоими источниками параллельно.
     *
     * @throws GeocoderUnavailableException если источник недоступен (сбой сети/таймаут/некорректный ответ)
     */
    public GeocodePair geocode(String address) {
        var yandex = geocodeAsync(yandexClient, address);
        var dadata = geocodeAsync(dadataClient, address);

        awaitCompletion(yandex, dadata);

        GeocodeResult yandexResult = yandex.join();
        GeocodeResult dadataResult = dadata.join();
        log.debug("Geocoded '{}': yandex - {}, dadata - {}", address, yandexResult, dadataResult);

        return new GeocodePair(yandexResult, dadataResult);
    }

    private CompletableFuture<GeocodeResult> geocodeAsync(GeocoderClient client, String address) {
        return CompletableFuture.supplyAsync(() ->
                client.geocode(address).orElse(null),
                geocoderExecutor);
    }

    private void awaitCompletion(CompletableFuture<?>... futures) {
        try {
            CompletableFuture.allOf(futures).join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            throw e;
        }
    }
}
