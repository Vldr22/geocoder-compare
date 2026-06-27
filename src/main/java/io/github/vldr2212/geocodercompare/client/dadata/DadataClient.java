package io.github.vldr2212.geocodercompare.client.dadata;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.client.GeocoderClient;
import io.github.vldr2212.geocodercompare.exception.GeocoderUnavailableException;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.model.enums.GeocoderProvider;
import io.github.vldr2212.geocodercompare.properties.DadataProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Component
@EnableConfigurationProperties(DadataProperties.class)
public class DadataClient implements GeocoderClient {

    private static final String SECRET_HEADER = "X-Secret";
    private static final String TOKEN_PREFIX = "Token ";

    private final WebClient webClient;
    private final DadataMapper mapper;

    public DadataClient(WebClient geocoderWebClient, DadataProperties properties, DadataMapper mapper) {
        this.webClient = geocoderWebClient.mutate()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + properties.apiKey())
                .defaultHeader(SECRET_HEADER, properties.secret())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.mapper = mapper;
    }

    @Override
    public GeocoderProvider provider() {
        return GeocoderProvider.DADATA;
    }

    @Override
    public Optional<GeocodeResult> geocode(String address) {
        try {
            return fetchGeoResults(address).stream()
                    .findFirst()
                    .map(mapper::toGeocodeResult)
                    .filter(result -> result.precision() != GeocodePrecision.NONE);
        } catch (RuntimeException e) {
            throw new GeocoderUnavailableException(provider(), e);
        }
    }

    private List<DadataGeoResult> fetchGeoResults(String address) {
        return webClient.post()
                .bodyValue(List.of(address))
                .retrieve()
                .bodyToFlux(DadataGeoResult.class)
                .collectList()
                .block();
    }
}
