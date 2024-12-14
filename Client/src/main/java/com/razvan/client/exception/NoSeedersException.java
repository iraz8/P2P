package com.razvan.client.exception;

public class NoSeedersException extends RuntimeException {
    public NoSeedersException() {
        super("No seeders available for the requested file.");
    }
}