package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@SuperBuilder
public class UserDto {
    @NotBlank(groups = Create.class)
    String name;
    @NotBlank(groups = Create.class) @Email(groups = Create.class) @Email(groups = Update.class)
    String email;
}
