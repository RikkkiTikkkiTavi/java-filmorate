package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@Service
public class UserService {
    private final UserStorage storage;

    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User getUserById(int id) {
        return storage.findUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        storage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        storage.removeFriend(userId, friendId);
    }

    public List<User> getMutualFriends(int userId, int friendId) {
        return storage.getMutualFriends(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return storage.getUserFriends(userId);
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        UserValidator.checkUser(user);
        return storage.update(user);
    }
}
