package io.github.vldr2212.geocodercompare.client.dadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadataGeoResult(
        @JsonProperty("geo_lat")
        String geoLat,

        @JsonProperty("geo_lon")
        String geoLon,

        @JsonProperty("qc_geo")
        Integer qcGeo
) {
}
