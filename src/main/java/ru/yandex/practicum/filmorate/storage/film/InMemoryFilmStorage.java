package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Map<Integer, Film> findAllMap() {
        return films;
    }

    @Override
    public List<Film> findAll() {
        log.debug("Получен запрос GET");
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        log.info("Получен запрос POST");
        FilmValidator.checkFilm(film);
        film.setId(id);
        film.setLikes(new TreeSet<>());
        films.put(id, film);
        id++;
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.debug("Получен запрос PUT");
        FilmValidator.checkFilm(film);
        FilmValidator.checkId(films, film);
        if (film.getLikes() == null) {
            film.setLikes(new TreeSet<>());
        }
        int id = film.getId();
        films.put(id, film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }
}
