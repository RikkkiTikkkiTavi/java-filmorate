package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User findUserById(int id);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getUserFriends(int userId);

    List<User> getMutualFriends(int userId, int friendId);
}
