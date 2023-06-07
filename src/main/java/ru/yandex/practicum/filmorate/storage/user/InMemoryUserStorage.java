package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public List<User> findAll() {
        log.info("Получен запрос GET");
        log.info("Количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        log.info("Получен запрос POST");
        UserValidator.checkUser(user);
        user.setId(id);
        users.put(id, user);
        id++;
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Получен запрос PUT");
        UserValidator.checkUser(user);
        UserValidator.checkId(findAll(), user);
        users.put(user.getId(), user);
        log.debug("Пользователь обновлен: {}", user);
        return user;
    }
}
