package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class FilmController {
    private final FilmService service;

    @GetMapping("/films")
    public List<Film> findAll() {
        return service.findAll();
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        return service.update(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable("id") int id) {
        return service.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("userId") int userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int id,
                           @PathVariable("userId") int userId) {
        service.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return service.getTopLikesFilms(count);
    }
}

