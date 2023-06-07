package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MPANotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage storage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage")FilmStorage storage) {
        this.storage = storage;
    }

    public Film getFilmById(int filmId) {
        FilmValidator.checkId(findAll(), filmId);
        return findAllMap().get(filmId);
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
        FilmValidator.checkFilm(film);
        return storage.create(film);
    }

    public Film update(Film film) {
        FilmValidator.checkFilm(film);
        FilmValidator.checkId(findAll(), film);
        return storage.update(film);
    }

    public List<Genre> findGenres() {
        return storage.findGenres();
    }

    public List<MPA> findMPA() {
        return storage.findMPA();
    }

    public MPA getMPAById(int id) {
        for (MPA mpa : findMPA()) {
            if (mpa.getId() == id) {
                return mpa;
            } else {
                throw new MPANotFoundException("Рейтинга с таким id не существует");
            }
        }
        return null;
    }

    public Genre getGenreById(int id) {
        for (Genre genre : findGenres()) {
            if (genre.getId() == id) {
                return genre;
            } else {
                throw new GenreNotFoundException("Жанра с таким id не существует");
            }
        }
        return null;
    }

    private Map<Integer, Film> findAllMap() {
        Map<Integer, Film> filmMap = new HashMap<>();
        for (Film film : findAll()) {
            filmMap.put(film.getId(), film);
        }
        return filmMap;
    }
}
