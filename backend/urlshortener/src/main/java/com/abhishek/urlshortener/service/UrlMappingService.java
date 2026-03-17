package com.abhishek.urlshortener.service;

import com.abhishek.urlshortener.dto.CreateShortUrlRequest;
import com.abhishek.urlshortener.dto.CreateShortUrlResponse;
import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.exception.InvalidUrlException;
import com.abhishek.urlshortener.exception.ShortUrlNotFoundException;
import com.abhishek.urlshortener.exception.ShortUrlGoneException;
import com.abhishek.urlshortener.enums.UrlStatusFilter;
import com.abhishek.urlshortener.repository.UrlMappingRepository;
import com.abhishek.urlshortener.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlMappingService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    public CreateShortUrlResponse generateShortCode(CreateShortUrlRequest request) {
        validateRequest(request.getOriginalUrl());
        String shortCode = generateShortCode();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(request.getOriginalUrl());
        urlMapping.setShortUrl(shortCode);
        urlMapping.setCreatedAt(LocalDateTime.now());
        urlMapping.setExpiresAt(LocalDateTime.now().plusDays(5));
        urlMapping.setClickCount(0);
        urlMapping.setActive(true);
        urlMappingRepository.save(urlMapping);
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String shortUrl = normalizedBase + "/" + shortCode;
        return new CreateShortUrlResponse(request.getOriginalUrl(), shortCode, shortUrl);
    }

    public UrlMapping resolveByShortCode(String shortCode) {
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrl(shortCode);
        if (!urlMapping.isPresent()) {
            throw new ShortUrlNotFoundException("Short URL not found: " + shortCode);
        }

        LocalDateTime now = LocalDateTime.now();
        if (!urlMapping.get().isActive()
                || (urlMapping.get().getExpiresAt() != null && urlMapping.get().getExpiresAt().isBefore(now))) {
            throw new ShortUrlGoneException("Short URL is inactive or expired: " + shortCode);
        }

        urlMapping.get().setClickCount(urlMapping.get().getClickCount() + 1);
        urlMappingRepository.save(urlMapping.get());

        return urlMapping.get();
    }

    public List<UrlMapping> fetchAllCreatedUrls() {
        return urlMappingRepository.findAll();
    }

    /**
     * Fetches URL mappings with required status filter, pagination, and sort by createdAt descending.
     */
    public Page<UrlMapping> fetchAllCreatedUrls(UrlStatusFilter status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDateTime now = LocalDateTime.now();
        return switch (status) {
            case ACTIVE -> urlMappingRepository.findAllActive(now, pageable);
            case EXPIRED -> urlMappingRepository.findAllExpired(now, pageable);
            case ALL -> urlMappingRepository.findAll(pageable);
        };
    }

    private void validateRequest(String originalUrl) {
        try {
            URI.create(originalUrl);
        } catch (Exception e) {
            throw new InvalidUrlException("Invalid URL: " + originalUrl);
        }
    }

    private String generateShortCode() {
        String shortCode;
        do {
            shortCode = ShortCodeGenerator.generate();
        } while (urlMappingRepository.existsByShortUrl(shortCode));
        return shortCode;
    }
}
