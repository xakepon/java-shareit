package ru.practicum.shareit.item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ItemDTO {

    public Long id;

    public Long requestId;

    @NotBlank
    public String name;

    @NotBlank
    public String description;

    @NotNull
    public Boolean available;
}
