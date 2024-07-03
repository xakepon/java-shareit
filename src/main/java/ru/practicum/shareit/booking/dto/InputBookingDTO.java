package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputBookingDTO {

    @NotNull
    private Long itemId;

    @Future
    @NotNull
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;
}
