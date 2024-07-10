package ru.practicum.shareit.exceptionTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

class ValidationExceptionTest {

    @Test
    void setNotValidData() {
        ValidationException exception = new ValidationException("message");
    }

}
