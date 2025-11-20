package xland.s7c7icu.client.api;

import java.io.IOException;

public class HashMismatchException extends IOException {
    public HashMismatchException(String message) {
        super(message);
    }

    static HashMismatchException of(String expected, String actual, String algorithm) {
        return new HashMismatchException("Expected hash '" + algorithm + "' to be " + expected + ", got " + actual);
    }
}
