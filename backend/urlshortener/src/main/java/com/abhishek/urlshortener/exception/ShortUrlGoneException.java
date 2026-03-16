package com.abhishek.urlshortener.exception;

/**
 * Exception thrown when a short URL is gone because it is inactive or expired.
 */
public class ShortUrlGoneException extends RuntimeException {

    public ShortUrlGoneException(String message) {
        super(message);
    }
}