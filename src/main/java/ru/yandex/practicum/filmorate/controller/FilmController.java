package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private int id = 1;

    @GetMapping("/films")
    public List<Film> findAll() {
        log.debug("Получен запрос GET");
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос POST");
        FilmValidator.checkFilm(film);
        film.setId(id);
        films.put(id, film);
        id++;
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        log.debug("Получен запрос PUT");
        FilmValidator.checkFilm(film);
        FilmValidator.checkId(films, film);
        films.put(film.getId(), film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }
}

