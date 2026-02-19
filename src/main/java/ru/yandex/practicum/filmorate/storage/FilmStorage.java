package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;

import java.util.Collection;
import java.util.List;


public interface FilmStorage {
    FilmDto create(Film film);

    FilmDto update(Film film);

    Collection<FilmDto> getFilms();

    FilmDto getFilmById(long id);

    List<FilmDto> getPopularFilms(int count);

    FilmDto addLike(long filmId, long userId);

    FilmDto removeLike(long filmId, long userId);

    List<Genre> getGenres();

    Genre getGenre(long id);

    List<Mpa> getMpas();

    Mpa findMpaById(long id);

}
