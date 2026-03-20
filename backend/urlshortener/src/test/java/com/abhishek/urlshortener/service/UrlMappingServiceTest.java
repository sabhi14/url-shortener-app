package com.abhishek.urlshortener.service;

import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.exception.ShortUrlNotFoundException;
import com.abhishek.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlMappingServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlMappingService urlMappingService;

    @Test
    void toggleActiveStatus_switchesTrueToFalse_andSaves() {
        UrlMapping mapping = new UrlMapping(
                1L,
                "https://example.com/long",
                "abc123",
                LocalDateTime.now(),
                null,
                0L,
                true);
        when(urlMappingRepository.findById(1L)).thenReturn(Optional.of(mapping));
        when(urlMappingRepository.save(mapping)).thenReturn(mapping);

        UrlMapping result = urlMappingService.toggleActiveStatus(1L);

        assertFalse(result.isActive());
        verify(urlMappingRepository).save(mapping);
    }

    @Test
    void toggleActiveStatus_switchesFalseToTrue_andSaves() {
        UrlMapping mapping = new UrlMapping(
                2L,
                "https://example.com/long",
                "xyz789",
                LocalDateTime.now(),
                null,
                2L,
                false);
        when(urlMappingRepository.findById(2L)).thenReturn(Optional.of(mapping));
        when(urlMappingRepository.save(mapping)).thenReturn(mapping);

        UrlMapping result = urlMappingService.toggleActiveStatus(2L);

        assertTrue(result.isActive());
        verify(urlMappingRepository).save(mapping);
    }

    @Test
    void toggleActiveStatus_throwsNotFound_whenIdDoesNotExist() {
        when(urlMappingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ShortUrlNotFoundException.class, () -> urlMappingService.toggleActiveStatus(999L));
    }
}
