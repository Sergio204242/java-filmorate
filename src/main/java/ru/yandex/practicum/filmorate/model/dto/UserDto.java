package ru.yandex.practicum.filmorate.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDto {
    private long id;

    private String email;

    private String login;

    private String name;

    private LocalDate birthday;

    private Set<Integer> friends;
}
