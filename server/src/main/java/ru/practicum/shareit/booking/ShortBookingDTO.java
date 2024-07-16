package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ShortBookingDTO {

    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
