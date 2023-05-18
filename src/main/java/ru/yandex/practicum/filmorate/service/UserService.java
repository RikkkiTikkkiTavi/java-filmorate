package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User getUserById(int id) {
        UserValidator.checkId(storage.findAllMap(), id);
        return storage.findAllMap().get(id);
    }

    public void addFriend(int userId, int friendId) {
        User userOne = getUserById(userId);
        User userTwo = getUserById(friendId);
        Set<Integer> userOneFriends = new TreeSet<>();
        Set<Integer> userTwoFriends = new TreeSet<>();
        if (userOne.getFriends() != null) {
            userOneFriends = userOne.getFriends();
        }
        if (userTwo.getFriends() != null) {
            userTwoFriends = userTwo.getFriends();
        }
        userOneFriends.add(friendId);
        userTwoFriends.add(userId);
        userOne.setFriends(userOneFriends);
        userTwo.setFriends(userTwoFriends);
        storage.update(userOne);
        storage.update(userTwo);
    }

    public void removeFriend(int userId, int friendId) {
        User userOne = getUserById(userId);
        User userTwo = getUserById(friendId);
        if (userOne.getFriends() != null) {
            Set<Integer> userOneFriends = userOne.getFriends();
            userOneFriends.remove(friendId);
            userOne.setFriends(userOneFriends);
            storage.update(userOne);
        }
        if (userTwo.getFriends() != null) {
            Set<Integer> userTwoFriends = userTwo.getFriends();
            userTwoFriends.remove(friendId);
            userTwo.setFriends(userTwoFriends);
            storage.update(userTwo);
        }
    }

    public List<User> getMutualFriends(int userId, int friendId) {
        User userOne = getUserById(userId);
        User userTwo = getUserById(friendId);
        List<User> mutualFriends = new ArrayList<>();
        if (userOne.getFriends() != null && userTwo.getFriends() != null) {
            List<Integer> userOneFriends = new ArrayList<>(getUserById(userId).getFriends());
            List<Integer> userTwoFriends = new ArrayList<>(getUserById(friendId).getFriends());
            for (int id : userOneFriends) {
                if (userTwoFriends.contains(id)) {
                    mutualFriends.add(getUserById(id));
                }
            }
        }
        return mutualFriends;
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        List<Integer> friendsId = new ArrayList<>(getUserById(userId).getFriends());
        for (int id : friendsId) {
            friends.add(getUserById(id));
        }
        return friends;
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.update(user);
    }
}
