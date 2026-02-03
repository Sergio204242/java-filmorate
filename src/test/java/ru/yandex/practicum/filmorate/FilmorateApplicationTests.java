package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
    private FilmController filmController;
    private UserController userController;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        userController = new UserController();
    }

    @Test
    void createFilm() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2004, 3, 27));

        Film createdFilm = filmController.create(film);

        assertEquals(film, createdFilm);
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void createFilmWithBadReleaseDate() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(1800, 3, 27));

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film));

        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895", exception.getMessage());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2004, 3, 27));

        Film createdFilm = filmController.create(film);

        Film film1 = new Film();
        film1.setId(film.getId());
        film1.setName("Interstellar");
        film1.setDescription("Fantastica");
        film1.setDuration(260);
        film1.setReleaseDate(LocalDate.of(2014, 4, 14));

        Film updatedFilm = filmController.update(film1);

        assertEquals(film, updatedFilm);
    }

    @Test
    void updateFilmWithBadReleaseDate() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Drama");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2004, 3, 27));

        Film createdFilm = filmController.create(film);

        Film film1 = new Film();
        film1.setId(film.getId());
        film1.setName("Interstellar");
        film1.setDescription("Fantastica");
        film1.setDuration(260);
        film1.setReleaseDate(LocalDate.of(1800, 4, 14));

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.update(film1));

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

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.update(film));

        assertEquals("Фильм с таким id не существует", exception.getMessage());
    }

    @Test
    void createUser() {
        User user = new User();
        user.setLogin("Sergio204");
        user.setName("Sergey");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        User createdUser = userController.create(user);

        assertEquals(user, createdUser);
        assertEquals(1, userController.getUsers().size());
    }

    @Test
    void createUserWithNullName() {
        User user = new User();
        user.setLogin("Sergio204");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        User createdUser = userController.create(user);

        assertEquals(user, createdUser);
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setLogin("Sergio204");
        user.setName("Sergey");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        User createdUser = userController.create(user);

        User user1 = new User();
        user1.setId(user.getId());
        user1.setLogin("Dimas777");
        user1.setName("Dima");
        user1.setBirthday(LocalDate.of(2002, 3, 27));
        user1.setEmail("learningjava777@gmail.com");

        User updatedUser = userController.update(user1);

        assertEquals(user1, updatedUser);
    }

    @Test
    void updateNonExistUser() {
        User user = new User();
        user.setId(3);
        user.setLogin("Sergio204");
        user.setName("Sergey");
        user.setBirthday(LocalDate.of(2004, 3, 27));
        user.setEmail("learningjava@gmail.com");

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.update(user));

        assertEquals("Пользователь с таким id не существует", exception.getMessage());
    }
}
