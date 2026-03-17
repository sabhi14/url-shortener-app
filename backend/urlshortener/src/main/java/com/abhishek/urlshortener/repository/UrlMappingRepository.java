package com.abhishek.urlshortener.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abhishek.urlshortener.entity.UrlMapping;

/**
 * Spring Data JPA repository for {@link UrlMapping} entities.
 * Provides CRUD operations and custom queries for short URL lookups.
 */
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    /**
     * Finds a URL mapping by its short code.
     *
     * @param shortUrl the short code (e.g. path segment of the short URL)
     * @return the mapping if found, otherwise empty
     */
    Optional<UrlMapping> findByShortUrl(String shortUrl);

    /**
     * Checks whether a short code is already in use.
     *
     * @param shortUrl the short code to check
     * @return true if a mapping with this short code exists
     */
    boolean existsByShortUrl(String shortUrl);

    /**
     * Active mappings only: active = true and (expiresAt is null or expiresAt >=
     * now).
     */
    @Query("SELECT u FROM UrlMapping u WHERE u.active = true AND (u.expiresAt IS NULL OR u.expiresAt >= :now)")
    Page<UrlMapping> findAllActive(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Expired/inactive mappings: active = false or (expiresAt is not null and
     * expiresAt &lt; now).
     */
    @Query("SELECT u FROM UrlMapping u WHERE u.active = false OR (u.expiresAt IS NOT NULL AND u.expiresAt < :now)")
    Page<UrlMapping> findAllExpired(@Param("now") LocalDateTime now, Pageable pageable);
}
