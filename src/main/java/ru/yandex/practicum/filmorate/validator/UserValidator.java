package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
public class UserValidator {

    public static void checkUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public static void checkId(Map<Integer, User> users, User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователя с id:" + user.getId() + " не существует");
        }
    }

    public static void checkId(Map<Integer, User> users, int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователя с id:" + id + " не существует");
        }
    }
}
