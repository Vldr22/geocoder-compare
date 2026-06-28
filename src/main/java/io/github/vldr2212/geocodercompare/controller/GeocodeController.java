package io.github.vldr2212.geocodercompare.controller;

import io.github.vldr2212.geocodercompare.dto.request.AddressRequest;
import io.github.vldr2212.geocodercompare.dto.response.GeocodingComparisonResponse;
import io.github.vldr2212.geocodercompare.mapper.GeocodingComparisonMapper;
import io.github.vldr2212.geocodercompare.service.ComparisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geocode")
public class GeocodeController {

    private final ComparisonService comparisonService;
    private final GeocodingComparisonMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeocodingComparisonResponse compare(@Valid @RequestBody AddressRequest request) {
        return mapper.toGeocodingResponse(comparisonService.compare(request.address()));
    }

    @GetMapping("/{publicId}")
    public GeocodingComparisonResponse getByPublicId(@PathVariable UUID publicId) {
        return mapper.toGeocodingResponse(comparisonService.getByPublicId(publicId));
    }
}