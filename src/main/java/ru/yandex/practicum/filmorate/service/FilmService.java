package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final ValidationService validationService;

    public FilmService(FilmStorage filmStorage, ValidationService validationService) {
        this.filmStorage = filmStorage;
        this.validationService = validationService;
    }

    public void addLike(long filmId, long userId) {
        validationService.validateFilmIdAndUserId(filmId, userId);

        filmStorage.getFilmById(filmId).getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        validationService.validateFilmIdAndUserId(filmId, userId);

        filmStorage.getFilmById(filmId).getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .toList();
    }
}
