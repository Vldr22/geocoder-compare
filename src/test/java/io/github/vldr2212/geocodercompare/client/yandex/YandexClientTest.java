package io.github.vldr2212.geocodercompare.client.yandex;

import io.github.vldr2212.geocodercompare.client.GeocodeResult;
import io.github.vldr2212.geocodercompare.exception.GeocoderUnavailableException;
import io.github.vldr2212.geocodercompare.model.enums.GeocodePrecision;
import io.github.vldr2212.geocodercompare.properties.YandexProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("YandexClient - получение координат через Yandex API")
class YandexClientTest {

    private static String foundJson;
    private static String emptyJson;

    private MockWebServer server;
    private YandexClient client;

    @BeforeAll
    static void loadFixtures() {
        foundJson = readFixture("fixtures/yandex/found.json");
        emptyJson = readFixture("fixtures/yandex/empty.json");
    }

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        YandexProperties properties = new YandexProperties(server.url("/").toString(), "test-key");
        client = new YandexClient(WebClient.builder().build(), properties, new YandexMapper());
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    @DisplayName("geocode - отправляет GET-запрос с ключом и адресом")
    void geocode_shouldSendRequestWithKeyAndAddress() throws InterruptedException {
        // given
        server.enqueue(jsonResponse(foundJson));

        // when
        client.geocode("Moscow");

        // then
        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath())
                .contains("apikey=test-key")
                .contains("geocode=Moscow");
    }

    @Test
    @DisplayName("geocode - возвращает результат когда API нашёл адрес")
    void geocode_shouldReturnResult_whenAddressFound() {
        // given
        server.enqueue(jsonResponse(foundJson));

        // when
        Optional<GeocodeResult> result = client.geocode("Москва Сухонская 11");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().coordinates().latitude()).isEqualByComparingTo("55.878256");
        assertThat(result.get().coordinates().longitude()).isEqualByComparingTo("37.653720");
        assertThat(result.get().precision()).isEqualTo(GeocodePrecision.HOUSE);
    }

    @Test
    @DisplayName("geocode - возвращает пусто когда API не нашёл адрес")
    void geocode_shouldReturnEmpty_whenAddressNotFound() {
        // given
        server.enqueue(jsonResponse(emptyJson));

        // when
        Optional<GeocodeResult> result = client.geocode("несуществующий");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("geocode - бросает ошибку когда API ответил сбоем")
    void geocode_shouldThrow_whenApiFails() {
        // given
        server.enqueue(new MockResponse().setResponseCode(500));

        // when & then
        assertThatThrownBy(() -> client.geocode("Москва"))
                .isInstanceOf(GeocoderUnavailableException.class);
    }

    private MockResponse jsonResponse(String body) {
        return new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);
    }

    private static String readFixture(String path) {
        try (InputStream input = YandexClientTest.class.getClassLoader().getResourceAsStream(path)) {
            return new String(Objects.requireNonNull(input).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}