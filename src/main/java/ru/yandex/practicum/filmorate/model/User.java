package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
        private Set<Integer> friends;
        private int id;
        private final String email;
        private final String login;
        private String name;
        private final LocalDate birthday;
}
