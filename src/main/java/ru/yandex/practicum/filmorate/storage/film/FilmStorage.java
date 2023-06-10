package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    List<Genre> findGenres();

    List<Mpa> findMPA();

    Film findFilmById(int id);
    Genre findGenreById(int id);
    Mpa findMpaById(int id);
    List<Film> getTopLikesFilms(int count);
    void addLike(int filmId, int userId);
    void deleteLike(int filmId, int userId);
}
