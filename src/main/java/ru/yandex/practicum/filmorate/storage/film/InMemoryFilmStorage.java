package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@Component
@Qualifier("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void deleteLike(int filmId, int userId) {

    }

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public List<Film> findAll() {
        log.debug("Получен запрос GET");
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Genre> findGenres() {
        return null;
    }

    @Override
    public List<Mpa> findMpa() {
        return null;
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
        if (film.getLikes() == null) {
            film.setLikes(new TreeSet<>());
        }
        int id = film.getId();
        films.put(id, film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public Film findFilmById(int id) {
        return null;
    }

    @Override
    public Genre findGenreById(int id) {
        return null;
    }

    @Override
    public Mpa findMpaById(int id) {
        return null;
    }

    @Override
    public List<Film> getTopLikesFilms(int count) {
        return null;
    }
}