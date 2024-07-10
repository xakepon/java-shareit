package ru.practicum.shareit.exceptionTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.AlreadyExistsException;

class AlreadyExistsExceptionTest {

    @Test
    void setInvalidState() {
        AlreadyExistsException exception = new AlreadyExistsException("message");
    }

}
