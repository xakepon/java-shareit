package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortBookingDTO {
    private Long id;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}
