package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage storage;

    public Film getFilmById(int filmId) {
        FilmValidator.checkId(storage.findAllMap(), filmId);
        return storage.findAllMap().get(filmId);
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        storage.update(film);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        if (film.getLikes() != null) {
            Set<Integer> likes = film.getLikes();
            FilmValidator.checkLikes(likes, userId);
            likes.remove(userId);
            film.setLikes(likes);
            storage.update(film);
        }
    }

    public List<Film> getTopLikesFilms(int count) {
        return storage.findAll().stream().sorted(((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())).limit(count)
                        .collect(Collectors.toList());
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }
}
