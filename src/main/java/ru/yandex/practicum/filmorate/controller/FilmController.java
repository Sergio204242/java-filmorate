package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping
    public Collection<FilmDto> getFilms() {
        return filmStorage.getFilms();
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmStorage.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable(value = "id") long id) {
        return filmStorage.getFilmById(id);
    }

    @PutMapping("/{film_id}/like/{id}")
    public FilmDto addLike(@PathVariable("film_id") long film_id, @PathVariable("id") long id) {
        return filmStorage.addLike(film_id, id);
    }

    @DeleteMapping("/{film_id}/like/{id}")
    public FilmDto removeLike(@PathVariable("film_id") long film_id, @PathVariable("id") long id) {
        return filmStorage.removeLike(film_id, id);
    }
}

