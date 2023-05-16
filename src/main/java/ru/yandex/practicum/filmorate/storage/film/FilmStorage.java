package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> findAll();

    Map<Integer, Film> findAllMap();

    Film create(Film film);

    Film update(Film film);
}
