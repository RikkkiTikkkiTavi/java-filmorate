package ru.yandex.practicum.filmorate.validator_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {

    Film film;

    @BeforeEach
    void initEach() {
        film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(200)
                .build();
    }

    @DisplayName("Название не может быть пустым")
    @Test
    void shouldThrowValidationExceptionFromFilmWithEmptyName() {
        film.setName("");

        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(film));
        assertEquals("Название не может быть пустым", e.getMessage());
    }

    @DisplayName("Максимальная длина описания — 200 символов")
    @Test
    void shouldThrowValidationExceptionWhenDescriptionMoreThan200Symbols() {
        film.setDescription("201".repeat(201));

        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(film));
        assertEquals("Максимальная длина описания — 200 символов", e.getMessage());
    }

    @DisplayName("Дата релиза — не раньше 28 декабря 1895")
    @Test
    void shouldThrowValidationExceptionWhenReleaseDateEarlierThanMovieBirthday() {
        film.setReleaseDate(LocalDate.of(1885, 12, 27));

        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895", e.getMessage());
    }

    @DisplayName("Продолжительность фильма должна быть положительной")
    @Test
    void shouldThrowValidationExceptionWhenFilmDurationNotPositive() {
        film.setDuration(0);

        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(film));
        assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
    }
}
