package com.abhishek.urlshortener.repository;

import com.abhishek.urlshortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
}
