package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage storage;

    @Autowired
    public FilmService(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

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
        return storage.findAll().stream().sorted(((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())).limit(count).
                collect(Collectors.toList());
    }
}
