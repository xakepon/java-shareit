package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputBookingDTO {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
