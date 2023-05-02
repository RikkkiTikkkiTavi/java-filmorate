package ru.yandex.practicum.filmorate.validator_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {

    @DisplayName("Название не может быть пустым")
    @Test
    void shouldThrowValidationExceptionFromFilmWithEmptyName() {
        Film emptyNameFilm = new Film("", "description", LocalDate.now(), 200);
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(emptyNameFilm));
        assertEquals("Название не может быть пустым", e.getMessage());
    }

    @DisplayName("Максимальная длина описания — 200 символов")
    @Test
    void shouldThrowValidationExceptionWhenDescriptionMoreThan200Symbols() {
        Film bigDescriptionFilm = new Film("name", "d".repeat(201), LocalDate.now(), 200);
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(bigDescriptionFilm));
        assertEquals("Максимальная длина описания — 200 символов", e.getMessage());
    }

    @DisplayName("Дата релиза — не раньше 28 декабря 1895")
    @Test
    void shouldThrowValidationExceptionWhenReleaseDateEarlierThanMovieBirthday() {
        Film oldFilm = new Film("name", "description", LocalDate.of(1885, 12, 27), 200);
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(oldFilm));
        assertEquals("Дата релиза — не раньше 28 декабря 1895", e.getMessage());
    }

    @DisplayName("Продолжительность фильма должна быть положительной")
    @Test
    void shouldThrowValidationExceptionWhenFilmDurationNotPositive() {
        Film oldFilm = new Film("name", "description", LocalDate.now(), 0);
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(oldFilm));
        assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
    }

    @DisplayName("Обновление несуществующего фильма должно выбросить исключение")
    @Test
    void shouldThrowValidationExceptionWhenUpdateNonExistUser() {
        Map<Integer, Film> films = new HashMap<>();
        Film film = new Film("name", "description", LocalDate.now(), 200);
        film.setId(1);
        films.put(film.getId(), film);
        Film updateFilm = new Film("name", "description", LocalDate.now(), 200);
        updateFilm.setId(9999);

        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkId(films, updateFilm));
        assertEquals("Обновить фильм с id:" + updateFilm.getId() + " невозможно по причине его отсутствия", e.getMessage());
    }
}
