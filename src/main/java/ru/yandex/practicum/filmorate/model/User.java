package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private final Set<Long> friends = new HashSet<>();

    private long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @NotNull
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
