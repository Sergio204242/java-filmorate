package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final ValidationService validationService;

    public UserService(UserStorage userStorage, ValidationService validationService) {
        this.userStorage = userStorage;
        this.validationService = validationService;
    }

    public void addFriend(long userId, long friendId) {
        validationService.validateUserIdAndFriendId(userId, friendId);

        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        validationService.validateUserIdAndFriendId(userId, friendId);

        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
    }

    public Set<Long> getFriends(long userId) {
        validationService.validateUserID(userId);

        return userStorage.getUserById(userId).getFriends();
    }

    public List<User> getUserCommonFriends(long id, long otherId) {
        List<User> commonFriends = new ArrayList<>();

        for (Long friendId : getFriends(id)) {
            if (getFriends(otherId).contains(friendId)) {
                commonFriends.add(userStorage.getUserById(friendId));
            }
        }

        return commonFriends;
    }

    public List<User> getUserFriends(long id) {
        List<User> friends = new ArrayList<>();

        for (Long friendId : getFriends(id)) {
            friends.add(userStorage.getUserById(friendId));
        }

        return friends;
    }
}
