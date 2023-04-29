package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping("/users")

    public List<User> findAll() {
        log.info("Получен запрос GET");
        log.info("Количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {
        log.info("Получен запрос POST");
        UserValidator.checkUser(user);
        user.setId(id);
        users.put(id, user);
        id++;
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        log.info("Получен запрос PUT");
        UserValidator.checkUser(user);
        UserValidator.checkId(users, user);
        users.put(user.getId(), user);
        log.debug("Пользователь обновлен: {}", user);
        return user;
    }
}
