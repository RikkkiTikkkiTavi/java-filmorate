package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage storage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage storage) {
        this.storage = storage;
    }

    public Film getFilmById(int filmId) {
        return storage.findFilmById(filmId);
    }

    public void addLike(int filmId, int userId) {
        storage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        storage.deleteLike(filmId, userId);
    }

    public List<Film> getTopLikesFilms(int count) {
        return storage.getTopLikesFilms(count);
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    public Film create(Film film) {
        FilmValidator.checkFilm(film);
        return storage.create(film);
    }

    public Film update(Film film) {
        FilmValidator.checkFilm(film);
        return storage.update(film);
    }

    public List<Genre> findGenres() {
        return storage.findGenres();
    }

    public List<Mpa> findMpa() {
        return storage.findMPA();
    }

    public Mpa getMpaById(int id) {
        return storage.findMpaById(id);
    }

    public Genre getGenreById(int id) {
        return storage.findGenreById(id);
    }
}
