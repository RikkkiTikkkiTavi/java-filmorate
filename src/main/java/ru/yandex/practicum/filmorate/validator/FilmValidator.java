package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Slf4j
public class FilmValidator {
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public static void checkFilm(Film film) {

        if (film.getName().isEmpty()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    public static void checkId(Map<Integer, Film> films, Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с id:" + film.getId() + "не существует");
        }
    }

    public static void checkId(Map<Integer, Film> films, int filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм с id:" + filmId + "не существует");
        }
    }

    public static void checkLikes(Set<Integer> likes, int id) {
        if (!likes.contains(id)) {
            throw new LikeNotFoundException("Лайк от пользователя с id:" + id + " не существует");
        }
    }
}
