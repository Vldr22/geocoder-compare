package io.github.vldr2212.geocodercompare.model.entity;

import io.github.vldr2212.geocodercompare.model.Coordinates;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "geocoding_comparisons")
public class GeocodingComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "public_id", unique = true, nullable = false, updatable = false, length = 36)
    private UUID publicId;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "yandex_lat", precision = 9, scale = Coordinates.SCALE)),
            @AttributeOverride(name = "longitude", column = @Column(name = "yandex_lon", precision = 9, scale = Coordinates.SCALE)),
            @AttributeOverride(name = "precision", column = @Column(name = "yandex_precision", length = 16)),
    })
    private ProviderGeocoding yandex;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "dadata_lat", precision = 9, scale = Coordinates.SCALE)),
            @AttributeOverride(name = "longitude", column = @Column(name = "dadata_lon", precision = 9, scale = Coordinates.SCALE)),
            @AttributeOverride(name = "precision", column = @Column(name = "dadata_precision", length = 16)),
    })
    private ProviderGeocoding dadata;

    @Column(name = "distance_meters", precision = 10, scale = 2)
    private BigDecimal distanceMeters;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}