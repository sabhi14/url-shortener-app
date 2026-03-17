package com.abhishek.urlshortener.controller;

import com.abhishek.urlshortener.dto.AnalyticsResponse;
import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.exception.ShortUrlNotFoundException;
import com.abhishek.urlshortener.service.UrlMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlMappingController.class)
class UrlMappingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlMappingService urlMappingService;

    @Test
    void getUrlMappingById_returns200WithBody_whenFound() throws Exception {
        UrlMapping mapping = new UrlMapping(
                1L,
                "https://example.com/long",
                "abc123",
                LocalDateTime.now(),
                null,
                0L,
                true
        );

        when(urlMappingService.getUrlMappingById(1L)).thenReturn(mapping);

        mockMvc.perform(get("/api/urls/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/long"))
                .andExpect(jsonPath("$.shortUrl").value("abc123"))
                .andExpect(jsonPath("$.clickCount").value(0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getUrlMappingById_returns404WithErrorBody_whenNotFound() throws Exception {
        when(urlMappingService.getUrlMappingById(999L))
                .thenThrow(new ShortUrlNotFoundException("Url mapping not found"));

        mockMvc.perform(get("/api/urls/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Url mapping not found"));
    }

    @Test
    void getAnalyticsById_returns200WithBody_whenFound() throws Exception {
        AnalyticsResponse analytics = new AnalyticsResponse(
                1L,
                "https://example.com/long",
                "abc123",
                LocalDateTime.of(2024, 1, 1, 12, 0),
                LocalDateTime.of(2024, 1, 6, 12, 0),
                5L,
                true
        );

        when(urlMappingService.getAnalyticsById(1L)).thenReturn(analytics);

        mockMvc.perform(get("/api/urls/1/analytics"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/long"))
                .andExpect(jsonPath("$.shortUrl").value("abc123"))
                .andExpect(jsonPath("$.clickCount").value(5))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getAnalyticsById_returns404WithErrorBody_whenNotFound() throws Exception {
        when(urlMappingService.getAnalyticsById(999L))
                .thenThrow(new ShortUrlNotFoundException("Url mapping not found"));

        mockMvc.perform(get("/api/urls/999/analytics"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Url mapping not found"));
    }
}

