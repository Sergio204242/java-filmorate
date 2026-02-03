package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма: {}", film);

        validateFilm(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен. id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма: {}", film);
        validateFilm(film);

        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            log.warn("Обновление несуществующего фильма");
            throw new ValidationException("Фильм с таким id не существует");
        }

        oldFilm.setDescription(film.getDescription());
        oldFilm.setName(film.getName());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setReleaseDate(film.getReleaseDate());

        log.info("Фильм успешно обновлён. id={}", film.getId());
        return oldFilm;
    }

    private void validateFilm(Film film) {
        final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            log.warn("Ошибка в дате релиза фильма. releaseDate={}, film={}", film.getReleaseDate(), film);
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
