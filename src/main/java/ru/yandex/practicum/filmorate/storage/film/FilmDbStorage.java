package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select film_id, name, description, release_date, duration from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Genre> findGenres() {
        String sqlQuery = "select genre_id, genre_name from genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public List<MPA> findMPA() {
        String sqlQuery = "select mpa_id, mpa_name from mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMPA);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into films(name, description, release_date, duration, mpa_id)" +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        String sqlQueryGenres = "insert into film_genres(film_id,genre_id)" + "values (?, ?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQueryGenres,
                        film.getId(),
                        genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ? , duration = ?, mpa_id = ?" +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        likesUpdate(film);
        genresUpdate(film);
        film.setGenres(getGenresForFilm(film.getId()));
        film.setMpa(getMpaForFilm(film.getId()));
        return film;
    }

    private void likesUpdate(Film film) {
        String sqlQueryDelete = "delete from likes where film_id = ?";
        jdbcTemplate.update(sqlQueryDelete, film.getId());

        String sqlQueryLikes = "insert into likes(film_id,user_id)" + "values (?, ?)";
        for (int id : film.getLikes()) {
            jdbcTemplate.update(sqlQueryLikes,
                    film.getId(),
                    id);
        }
    }

    private void genresUpdate(Film film) {
        String queryDeleteGenres = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(queryDeleteGenres, film.getId());

        String sqlQueryGenres = "insert into film_genres(film_id,genre_id)" + "values (?, ?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQueryGenres,
                        film.getId(),
                        genre.getId());
            }
        }
    }

    private Set<Integer> getLikesForFilm(int id) {
        Set<Integer> likes = new TreeSet<>();
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select user_id from likes where film_id = ?", id);
        while (likesRows.next()) {
            likes.add(likesRows.getInt("user_id"));
        }
        return likes;
    }

    private Set<Genre> getGenresForFilm(int id) {
        Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select f.genre_id, g.genre_name from film_genres as f join genres as g on f.genre_id = g.genre_id where f.film_id = ?", id);
        while (genresRows.next()) {
            Genre genre = new Genre();
            genre.setId(genresRows.getInt("genre_id"));
            genre.setName(genresRows.getString("genre_name"));
            genres.add(genre);
        }
        return genres;
    }

    private MPA getMpaForFilm(int id) {
        MPA mpa = MPA.builder().build();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select m.mpa_id, m.mpa_name from films as f join mpa as m on f.mpa_id = m.mpa_id where f.film_id = ?", id);
        if (mpaRows.next()) {
            mpa.setId(mpaRows.getInt("mpa_id"));
            mpa.setName(mpaRows.getString("mpa_name"));
        }
        return mpa;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .likes(getLikesForFilm(resultSet.getInt("film_id")))
                .genres(getGenresForFilm(resultSet.getInt("film_id")))
                .mpa(getMpaForFilm(resultSet.getInt("film_id")))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("genre_name"));
        return genre;
    }

    private MPA mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        MPA mpa = MPA.builder().build();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        return mpa;
    }
}