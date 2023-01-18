package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@SuperBuilder
public class UserDto {
    @Positive
    private long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class) @Email(groups = Create.class) @Email(groups = Update.class)
    private String email;
}
