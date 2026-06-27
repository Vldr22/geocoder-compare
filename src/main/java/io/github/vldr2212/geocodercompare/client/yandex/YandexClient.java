package io.github.vldr2212.geocodercompare.client.yandex;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.client.GeocoderClient;
import io.github.vldr2212.geocodercompare.exception.GeocoderUnavailableException;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.model.enums.GeocoderProvider;
import io.github.vldr2212.geocodercompare.properties.YandexProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
@EnableConfigurationProperties(YandexProperties.class)
public class YandexClient implements GeocoderClient {

    private static final String PARAM_API_KEY = "apikey";
    private static final String PARAM_GEOCODE = "geocode";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_LANG = "lang";
    private static final String PARAM_RESULTS = "results";
    private static final String FORMAT_JSON = "json";
    private static final String LANG_RU = "ru_RU";
    private static final int RESULTS_LIMIT = 1;

    private final WebClient webClient;
    private final YandexMapper mapper;
    private final String apiKey;

    public YandexClient(WebClient geocoderWebClient, YandexProperties properties, YandexMapper mapper) {
        this.webClient = geocoderWebClient.mutate()
                .baseUrl(properties.baseUrl())
                .build();
        this.apiKey = properties.apiKey();
        this.mapper = mapper;
    }

    @Override
    public GeocoderProvider provider() {
        return GeocoderProvider.YANDEX;
    }

    @Override
    public Optional<GeocodeResult> geocode(String address) {
        try {
            return firstGeoObject(fetchResponse(address))
                    .map(mapper::toGeocodeResult)
                    .filter(result -> result.precision() != GeocodePrecision.NONE);
        } catch (RuntimeException e) {
            throw new GeocoderUnavailableException(provider(), e);
        }
    }

    private YandexResponse fetchResponse(String address) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(PARAM_API_KEY, apiKey)
                        .queryParam(PARAM_GEOCODE, address)
                        .queryParam(PARAM_FORMAT, FORMAT_JSON)
                        .queryParam(PARAM_LANG, LANG_RU)
                        .queryParam(PARAM_RESULTS, RESULTS_LIMIT)
                        .build())
                .retrieve()
                .bodyToMono(YandexResponse.class)
                .block();
    }

    private Optional<YandexResponse.GeoObject> firstGeoObject(YandexResponse response) {
        return Optional.ofNullable(response)
                .map(YandexResponse::response)
                .map(YandexResponse.Response::geoObjectCollection)
                .map(YandexResponse.GeoObjectCollection::featureMembers)
                .flatMap(members -> members.stream().findFirst())
                .map(YandexResponse.FeatureMember::geoObject);
    }
}