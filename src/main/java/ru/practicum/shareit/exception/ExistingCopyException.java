package ru.practicum.shareit.exception;

public class ExistingCopyException extends RuntimeException {
    public ExistingCopyException(String message) {
        super(message);
    }
}
