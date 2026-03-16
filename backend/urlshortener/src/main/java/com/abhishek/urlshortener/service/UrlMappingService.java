package com.abhishek.urlshortener.service;

import com.abhishek.urlshortener.dto.CreateShortUrlRequest;
import com.abhishek.urlshortener.dto.CreateShortUrlResponse;
import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.exception.ShortUrlNotFoundException;
import com.abhishek.urlshortener.exception.ShortUrlGoneException;
import com.abhishek.urlshortener.repository.UrlMappingRepository;
import com.abhishek.urlshortener.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.net.URI;
import java.time.LocalDateTime;

@Service
public class UrlMappingService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

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
        return new CreateShortUrlResponse(request.getOriginalUrl(), shortCode, "http://localhost:8080/" + shortCode);
    }

    public UrlMapping resolveByShortCode(String shortCode) {
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrl(shortCode);
        if(!urlMapping.isPresent()) {
            throw new ShortUrlNotFoundException("Short URL not found: " + shortCode);
        }  

        LocalDateTime now = LocalDateTime.now();
        if (!urlMapping.get().isActive() || (urlMapping.get().getExpiresAt() != null && urlMapping.get().getExpiresAt().isBefore(now))) {
            throw new ShortUrlGoneException("Short URL is inactive or expired: " + shortCode);
        }

        urlMapping.get().setClickCount(urlMapping.get().getClickCount() + 1);
        urlMappingRepository.save(urlMapping.get());

        return urlMapping.get();
    }

    private void validateRequest(String originalUrl) {
        try {
            URI.create(originalUrl);
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL: " + originalUrl);
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
