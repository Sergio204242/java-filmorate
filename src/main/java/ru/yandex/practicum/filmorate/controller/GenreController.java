package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmStorage filmStorage;

    @Autowired
    public GenreController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable("id") long id) {
        return filmStorage.getGenre(id);
    }
}
