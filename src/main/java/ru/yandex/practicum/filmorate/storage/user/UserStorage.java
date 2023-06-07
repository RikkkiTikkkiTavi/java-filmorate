package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> findAll();

    Map<Integer, User> findAllMap();

    User create(User user);

    User update(User user);
}
