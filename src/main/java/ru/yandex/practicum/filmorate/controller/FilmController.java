package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final InMemoryFilmStorage storage;
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service, InMemoryFilmStorage storage) {
        this.storage = storage;
        this.service = service;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return storage.findAll();
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        return storage.create(film);
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        return storage.update(film);
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

