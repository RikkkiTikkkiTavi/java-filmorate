package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {

    private Set<Integer> likes;
    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;

}
