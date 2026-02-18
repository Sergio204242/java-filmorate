package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.UserDto;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDbStorage implements UserStorage {

    JdbcTemplate jdbcTemplate;
    UserRowMapper userRowMapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public UserDto create(User user) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String insertSql = "INSERT INTO users (email, login, name, birthday)" +
                " VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));

            return ps;
        }, keyHolder);

        Integer userId = keyHolder.getKeyAs(Integer.class);

        return getUserById(userId);
    }

    @Override
    public UserDto update(User user) {
        String updateSql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(updateSql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ?", user.getId());

        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            for (Integer friend_id : user.getFriends()) {
                jdbcTemplate.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?)",
                        user.getId(), friend_id);
            }
        }

        return getUserById(user.getId());
    }

    @Override
    public UserDto getUserById(long userId) {
        String userSql = "SELECT * FROM users WHERE id = ?";

        User userFromDb;
        try {
            userFromDb = jdbcTemplate.queryForObject(userSql, userRowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("Пользователь с id = " + userId + " не найден");
        }

        String friendsSql = "SELECT friend_id FROM friends WHERE user_id = ?";

        List<Integer> friendsFromDb = jdbcTemplate.queryForList(friendsSql, Integer.class, userId);

        userFromDb.getFriends().addAll(friendsFromDb);

        return UserMapper.mapToUserDto(userFromDb);
    }

    @Override
    public List<UserDto> getUsers() {

        String selectSql = "SELECT * FROM users";

        List<User> usersFromDb = jdbcTemplate.query(selectSql, userRowMapper);

        List<UserDto> usersDto = new ArrayList<>();

        for (User user : usersFromDb) {
            usersDto.add(UserMapper.mapToUserDto(user));
        }

        return usersDto;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);

        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        String sql =
                "INSERT INTO friends (user_id, friend_id) " +
                        "VALUES (?, ?)";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);

        String deleteSql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteSql, userId, friendId);
    }

    @Override
    public List<UserDto> getUserFriends(long userId) {
        getUserById(userId);

        String selectSql = "SELECT friend_id FROM friends WHERE user_id = ?";

        List<Integer> friendsIdFromDb = jdbcTemplate.queryForList(selectSql, Integer.class, userId);

        List<UserDto> userDtos = new ArrayList<>();

        for (Integer friendId : friendsIdFromDb) {
            userDtos.add(getUserById(friendId));
        }

        return userDtos;
    }

    @Override
    public List<UserDto> getCommonFriends(long userId, long otherId) {
        getUserById(userId);
        getUserById(otherId);

        String sql = " SELECT u.* " +
                "FROM users u " +
                "JOIN friends f1 ON u.id = f1.friend_id " +
                "JOIN friends f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? " +
                "AND f2.user_id = ? " +
                "ORDER BY u.id";

        List<User> usersFromDb = jdbcTemplate.query(sql, userRowMapper, userId, otherId);

        List<UserDto> userDtos = new ArrayList<>();

        for (User user : usersFromDb) {
            userDtos.add(UserMapper.mapToUserDto(user));
        }

        return userDtos;
    }
}
