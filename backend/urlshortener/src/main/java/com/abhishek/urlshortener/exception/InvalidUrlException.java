package com.abhishek.urlshortener.exception;

/**
 * Exception thrown when the provided original URL is invalid or cannot be parsed.
 */
public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String message) {
        super(message);
    }
}

