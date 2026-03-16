package com.abhishek.urlshortener.util;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

public class ShortCodeGenerator {

    // TODO: add constants (e.g. alphabet, length) if needed
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
    /**
     * Generates a new short code that can be used as the path segment of a short URL.
     *
     * @param length desired length of the short code (optional; use default if not needed)
     * @return a new short code string
     */
    public static String generate() {
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            shortCode.append(ALPHABET.charAt(index));
        }
        return shortCode.toString();
    }
}
