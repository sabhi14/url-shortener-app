package com.abhishek.urlshortener.enums;

/**
 * Filter for listing URL mappings by status.
 */
public enum UrlStatusFilter {
    /** Only active mappings: active = true and (no expiry or expiresAt >= now). */
    ACTIVE,
    /** Only expired/inactive: active = false or (expiresAt set and expiresAt &lt; now). */
    EXPIRED,
    /** No status filter; return all mappings. */
    ALL
}
