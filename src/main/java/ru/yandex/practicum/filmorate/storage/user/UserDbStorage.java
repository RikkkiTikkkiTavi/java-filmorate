package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("UserDbStorage")
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sqlQuery = "select user_id, name, email, birthday, login from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        String sqlQuery = "SELECT u.user_id, u.name, u.email, u.birthday, u.login FROM users u JOIN " +
                "friends f ON u.user_id = f.FRIEND_ID WHERE f.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    private Set<Integer> getFriends(int id) {
        String sqlQuery = "select friend_id from friends where user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToIdUser, id));
    }

    private Integer mapRowToIdUser(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("friend_id");
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .login(resultSet.getString("login"))
                .friends(getFriends(resultSet.getInt("user_id")))
                .build();
    }

    @Override
    public User create(User user) {
        UserValidator.checkUser(user);
        String sqlQuery = "insert into users(name, email, birthday, login)" +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setDate(3, java.sql.Date.valueOf(user.getBirthday()));
            stmt.setString(4, user.getLogin());
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        findUserById(user.getId());
        if (user.getFriends() == null) {
            user.setFriends(new TreeSet<>());
        }
        String sqlQuery = "update users set " +
                "name = ?, email = ?, birthday = ? , login = ?" +
                "where user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getBirthday(), user.getLogin(),
                user.getId());
        updateFriends(user);
        return user;
    }

    @Override
    public User findUserById(int id) {
        try {
            String sqlQuery = "select user_id, name, email, birthday, login from users where user_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (DataAccessException e) {
            throw new UserNotFoundException("Пользователь с таким id не существует");
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        String sqlQuery = "insert into friends(user_id, friend_id) values(?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "delete from friends where user_id = ? and friend_id = ?";
        if (jdbcTemplate.update(sqlQuery, userId, friendId) <= 0) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<User> getMutualFriends(int userId, int friendId) {
        String sqlQuery = "select u.user_id , u.name , u.email, u.login , u.birthday " +
                "from friends f join users u on f.friend_id = u.user_id " +
                "where f.user_id = ? and f.friend_id in(select friend_id from friends where user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, friendId);
    }

    private void updateFriends(User user) {
        String sqlQueryDelete = "delete from friends where user_id = ?";
        jdbcTemplate.update(sqlQueryDelete, user.getId());

        String sqlQueryLikes = "insert into friends(user_id,friend_id)" + "values (?, ?)";
        List<Integer> friends = new ArrayList<>(user.getFriends());
        jdbcTemplate.batchUpdate(sqlQueryLikes,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, user.getId());
                        ps.setInt(2, friends.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return friends.size();
                    }
                });
    }
}
