package io.github.vldr2212.geocodercompare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vldr2212.geocodercompare.dto.request.AddressRequest;
import io.github.vldr2212.geocodercompare.dto.response.GeocodingComparisonResponse;
import io.github.vldr2212.geocodercompare.exception.AddressNotFoundException;
import io.github.vldr2212.geocodercompare.exception.GeocoderUnavailableException;
import io.github.vldr2212.geocodercompare.exception.ResourceNotFoundException;
import io.github.vldr2212.geocodercompare.mapper.GeocodingComparisonMapper;
import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import io.github.vldr2212.geocodercompare.model.enums.GeocoderProvider;
import io.github.vldr2212.geocodercompare.service.ComparisonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GeocodeController.class)
@DisplayName("GeocodeController - REST API сравнения геокодеров")
class GeocodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ComparisonService comparisonService;

    @MockitoBean
    private GeocodingComparisonMapper mapper;

    @Test
    @DisplayName("POST /api/v1/geocode - 201 и тело когда сравнение успешно")
    void compare_shouldReturn201_whenSuccess() throws Exception {
        // given
        GeocodingComparisonResponse response = new GeocodingComparisonResponse(
                UUID.randomUUID(), "Москва Сухонская 11", null, null,
                BigDecimal.valueOf(47), true, Instant.now());
        when(comparisonService.compare(anyString())).thenReturn(new GeocodingComparison());
        when(mapper.toGeocodingResponse(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/geocode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddressRequest("Москва Сухонская 11"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("Москва Сухонская 11"))
                .andExpect(jsonPath("$.reliable").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/geocode - 400 когда адрес пустой")
    void compare_shouldReturn400_whenAddressBlank() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/geocode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddressRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.address").exists());
    }

    @Test
    @DisplayName("POST /api/v1/geocode - 422 когда адрес не найден")
    void compare_shouldReturn422_whenAddressNotFound() throws Exception {
        // given
        when(comparisonService.compare(anyString()))
                .thenThrow(AddressNotFoundException.forAddress("Москва Сухонская 11"));

        // when & then
        mockMvc.perform(post("/api/v1/geocode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddressRequest("Москва Сухонская 11"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/v1/geocode - 502 с провайдером когда геокодер недоступен")
    void compare_shouldReturn502_whenGeocoderUnavailable() throws Exception {
        // given
        when(comparisonService.compare(anyString()))
                .thenThrow(GeocoderUnavailableException.forProvider(GeocoderProvider.YANDEX, new RuntimeException()));

        // when & then
        mockMvc.perform(post("/api/v1/geocode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddressRequest("Москва"))))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.provider").value("YANDEX"));
    }

    @Test
    @DisplayName("GET /api/v1/geocode/{publicId} - 200 и тело когда запись существует")
    void getByPublicId_shouldReturn200_whenExists() throws Exception {
        // given
        UUID publicId = UUID.randomUUID();
        GeocodingComparisonResponse response = new GeocodingComparisonResponse(
                publicId, "Москва Сухонская 11", null, null,
                BigDecimal.valueOf(47), true, Instant.now());
        when(comparisonService.getByPublicId(publicId)).thenReturn(new GeocodingComparison());
        when(mapper.toGeocodingResponse(any())).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/geocode/{publicId}", publicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(publicId.toString()))
                .andExpect(jsonPath("$.address").value("Москва Сухонская 11"));
    }

    @Test
    @DisplayName("GET /api/v1/geocode/{publicId} - 404 когда записи нет")
    void getByPublicId_shouldReturn404_whenNotFound() throws Exception {
        // given
        UUID publicId = UUID.randomUUID();
        when(comparisonService.getByPublicId(publicId))
                .thenThrow(ResourceNotFoundException.byPublicId(publicId));

        // when & then
        mockMvc.perform(get("/api/v1/geocode/{publicId}", publicId))
                .andExpect(status().isNotFound());
    }
}
