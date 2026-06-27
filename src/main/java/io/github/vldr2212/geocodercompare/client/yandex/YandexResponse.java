package io.github.vldr2212.geocodercompare.client.yandex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YandexResponse(
        Response response
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            @JsonProperty("GeoObjectCollection")
            GeoObjectCollection geoObjectCollection
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeoObjectCollection(
            @JsonProperty("featureMember")
            List<FeatureMember> featureMembers
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FeatureMember(
            @JsonProperty("GeoObject")
            GeoObject geoObject
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeoObject(
            @JsonProperty("Point")
            Point point,

            @JsonProperty("metaDataProperty")
            MetaDataProperty metaDataProperty
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Point(
            @JsonProperty("pos")
            String pos
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MetaDataProperty(
            @JsonProperty("GeocoderMetaData")
            GeocoderMetaData geocoderMetaData
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeocoderMetaData(
            @JsonProperty("precision")
            String precision
    ) {
    }
}