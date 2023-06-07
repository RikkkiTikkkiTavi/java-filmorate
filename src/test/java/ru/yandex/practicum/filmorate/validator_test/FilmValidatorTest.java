package ru.yandex.practicum.filmorate.validator_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {

    @DisplayName("Название не может быть пустым")
    @Test
    void shouldThrowValidationExceptionFromFilmWithEmptyName() {
        Film emptyNameFilm = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(200)
                .build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(emptyNameFilm));
        assertEquals("Название не может быть пустым", e.getMessage());
    }

    @DisplayName("Максимальная длина описания — 200 символов")
    @Test
    void shouldThrowValidationExceptionWhenDescriptionMoreThan200Symbols() {
        Film bigDescriptionFilm = Film.builder()
                .name("name")
                .description("d".repeat(201))
                .releaseDate(LocalDate.now())
                .duration(200).build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(bigDescriptionFilm));
        assertEquals("Максимальная длина описания — 200 символов", e.getMessage());
    }

    @DisplayName("Дата релиза — не раньше 28 декабря 1895")
    @Test
    void shouldThrowValidationExceptionWhenReleaseDateEarlierThanMovieBirthday() {
        Film oldFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1885, 12, 27))
                .duration(200).build();

        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(oldFilm));
        assertEquals("Дата релиза — не раньше 28 декабря 1895", e.getMessage());
    }

    @DisplayName("Продолжительность фильма должна быть положительной")
    @Test
    void shouldThrowValidationExceptionWhenFilmDurationNotPositive() {
        Film oldFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(0)
                .build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.checkFilm(oldFilm));
        assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
    }

    @DisplayName("Обновление несуществующего фильма должно выбросить исключение")
    @Test
    void shouldThrowValidationExceptionWhenUpdateNonExistUser() {
        List<Film> films = new ArrayList<>();
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(200)
                .build();
        film.setId(1);
        films.add(film);
        Film updateFilm = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(200)
                .build();
        updateFilm.setId(9999);

        FilmNotFoundException e = assertThrows(
                FilmNotFoundException.class,
                () -> FilmValidator.checkId(films, updateFilm));
        assertEquals("Фильм с id:" + updateFilm.getId() + "не существует", e.getMessage());
    }
}
