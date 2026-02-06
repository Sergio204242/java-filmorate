package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);

        return filmStorage.getFilmById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void delete(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }
}
