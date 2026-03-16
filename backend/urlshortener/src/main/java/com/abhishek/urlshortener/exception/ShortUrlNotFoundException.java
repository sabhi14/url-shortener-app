package com.abhishek.urlshortener.exception;

/**
 * Exception thrown when a short URL cannot be resolved because it does not exist,
 * is inactive, or has expired.
 */
public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String message) {
        super(message);
    }
}

