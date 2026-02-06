package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class ValidationService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ValidationService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void validateUserID(long userId) {
        if (userId < 1) {
            throw new ValidationException("id пользователя должен быть больше нуля");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("id пользователя не найден");
        }
    }

    public void validateFilmIdAndUserId(long filmId, long userId) {
        if (userId < 1) {
            throw new ValidationException("id пользователя должен быть больше нуля");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("id пользователя не найден");
        }
        if (filmId < 1) {
            throw new ValidationException("id фильма должно быть больше нуля");
        }
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public void validateUserIdAndFriendId(long userId, long friendId) {
        if (userId < 1) {
            throw new ValidationException("id пользователя должен быть больше нуля");
        }
        if (friendId < 1) {
            throw new ValidationException("id друга должен быть больше нуля");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("id пользователя не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("id друга не найден");
        }
    }


}
