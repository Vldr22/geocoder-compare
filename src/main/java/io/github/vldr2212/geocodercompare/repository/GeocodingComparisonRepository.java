package io.github.vldr2212.geocodercompare.repository;

import io.github.vldr2212.geocodercompare.model.entity.GeocodingComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeocodingComparisonRepository extends JpaRepository<GeocodingComparison, Long> {

    Optional<GeocodingComparison> findByPublicId(UUID publicId);

}