package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select user_id, name, email, birthday, login from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private Set<Integer> getFriends(int id) {
        Set<Integer> friends = new TreeSet<>();
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select friend_id from friends where user_id = ?", id);
        while (likesRows.next()) {
            friends.add(likesRows.getInt("friend_id"));
        }
        return friends;
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
        UserValidator.checkUser(user);
        UserValidator.checkId(findAll(), user);
        if (user.getFriends() == null) {
            user.setFriends(new TreeSet<>());
        }
        String sqlQuery = "update users set " +
                "name = ?, email = ?, birthday = ? , login = ?" +
                "where user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getBirthday(), user.getLogin(), user.getId());
        updateFriends(user);
        return user;
    }

    private void updateFriends(User user) {
        String sqlQueryDelete = "delete from friends where user_id = ?";
        jdbcTemplate.update(sqlQueryDelete, user.getId());

        String sqlQueryLikes = "insert into friends(user_id,friend_id)" + "values (?, ?)";
        for (int id : user.getFriends()) {
            jdbcTemplate.update(sqlQueryLikes,
                    user.getId(),
                    id);
        }
    }
}
