package ru.practicum.shareit.exceptionTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.InvalidStateException;

class InvalidStateExceptionTest {

    @Test
    void setInvalidState() {
        InvalidStateException exception = new InvalidStateException("message");
    }

}
