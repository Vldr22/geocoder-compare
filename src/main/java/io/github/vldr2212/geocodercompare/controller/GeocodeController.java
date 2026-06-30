package io.github.vldr2212.geocodercompare.controller;

import io.github.vldr2212.geocodercompare.config.OpenApiConfig;
import io.github.vldr2212.geocodercompare.dto.request.AddressRequest;
import io.github.vldr2212.geocodercompare.dto.response.GeocodingComparisonResponse;
import io.github.vldr2212.geocodercompare.mapper.GeocodingComparisonMapper;
import io.github.vldr2212.geocodercompare.service.ComparisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_GEOCODE)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geocode")
public class GeocodeController {

    private final ComparisonService comparisonService;
    private final GeocodingComparisonMapper mapper;

    @Operation(summary = "Сравнить геокодирование адреса двумя источниками")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Сравнение создано"),
            @ApiResponse(responseCode = "400", description = "Невалидный адрес"),
            @ApiResponse(responseCode = "422", description = "Адрес не найден хотя бы одним источником"),
            @ApiResponse(responseCode = "502", description = "Геокодер недоступен")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeocodingComparisonResponse compare(@Valid @RequestBody AddressRequest request) {
        return mapper.toGeocodingResponse(comparisonService.compare(request.address()));
    }

    @Operation(summary = "Получить сохранённое сравнение по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сравнение найдено"),
            @ApiResponse(responseCode = "404", description = "Сравнение не найдено")
    })
    @GetMapping("/{publicId}")
    public GeocodingComparisonResponse getByPublicId(@PathVariable UUID publicId) {
        return mapper.toGeocodingResponse(comparisonService.getByPublicId(publicId));
    }
}