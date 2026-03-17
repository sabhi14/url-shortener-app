package com.abhishek.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

import com.abhishek.urlshortener.entity.UrlMapping;


/**
 * Response returned after successfully fetching all created short urls.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FetchAllCreatedUrls {
    private List<UrlMapping> urlMappings;
}