package ru.yandex.practicum.filmorate;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.DateException;
import ru.yandex.practicum.filmorate.mapper.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class,
        UserRowMapper.class, FilmExtractor.class})
class FilmorateApplicationTests {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Test
    void createFilm() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2004, 3, 27));

        FilmDto createdFilm = filmStorage.create(film);
        film.setId(createdFilm.getId());

        assertEquals(FilmMapper.mapToFilmDto(film), createdFilm);
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    void createFilmWithBadReleaseDate() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(1800, 3, 27));

        DateException exception = assertThrows(DateException.class,
                () -> filmStorage.create(film));

        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895", exception.getMessage());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2004, 3, 27));

        FilmDto createdFilm = filmStorage.create(film);

        Film film1 = new Film();
        film1.setId(film.getId());
        film1.setName("Interstellar");
        film1.setDescription("Fantastica");
        film1.setDuration(260);
        film1.setReleaseDate(LocalDate.of(2014, 4, 14));
        film1.setMpa(new Mpa(1, "G"));
        FilmDto updatedFilm = filmStorage.update(film1);

        assertEquals(FilmMapper.mapToFilmDto(film1), updatedFilm);
    }

    @Test
    void updateFilmWithBadReleaseDate() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2004, 3, 27));

        FilmDto createdFilm = filmStorage.create(film);

        Film film1 = new Film();
        film1.setId(film.getId());
        film1.setName("Interstellar");
        film1.setDescription("Fantastica");
        film1.setDuration(260);
        film1.setReleaseDate(LocalDate.of(1800, 4, 14));
        film1.setMpa(new Mpa(1, "G"));

        DateException exception = assertThrows(DateException.class,
                () -> filmStorage.update(film1));

        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895", exception.getMessage());
    }

    @Test
    void updateNonExistFilm() {
        Film film = new Film();
        film.setId(86);
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2004, 3, 27));
        film.setMpa(new Mpa(1, "G"));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmStorage.update(film));

        assertEquals("Фильм с id = 86 не найден", exception.getMessage());
    }

    @Test
    void createUser() {
        User user = new User();
        user.setLogin("Sergio204");
        user.setName("Sergey");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        UserDto createdUser = userStorage.create(user);
        user.setId(createdUser.getId());

        assertEquals(UserMapper.mapToUserDto(user), createdUser);
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    void createUserWithNullName() {
        User user = new User();
        user.setLogin("Sergio204");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        UserDto createdUser = userStorage.create(user);
        user.setId(createdUser.getId());

        assertEquals(UserMapper.mapToUserDto(user), createdUser);
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setLogin("Sergio204");
        user.setName("Sergey");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        UserDto createdUser = userStorage.create(user);

        User user1 = new User();
        user1.setId(createdUser.getId());
        user1.setLogin("Dimas777");
        user1.setName("Dima");
        user1.setBirthday(LocalDate.of(2002, 3, 27));
        user1.setEmail("learningjava777@gmail.com");

        UserDto updatedUser = userStorage.update(user1);

        assertEquals(UserMapper.mapToUserDto(user1), updatedUser);
    }

    @Test
    void updateNonExistUser() {
        User user = new User();
        user.setId(3);
        user.setLogin("Sergio204");
        user.setName("Sergey");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userStorage.update(user));

        assertEquals("Пользователь с id = 3 не найден", exception.getMessage());
    }
}