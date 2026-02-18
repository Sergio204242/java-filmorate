package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    UserDto create(User user);

    UserDto update(User user);

    UserDto getUserById(long user_id);

    Collection<UserDto> getUsers();

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<UserDto> getUserFriends(long userId);

    List<UserDto> getCommonFriends(long userId, long otherId);
}
