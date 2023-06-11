package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film findFilmById(int id) {
        String sqlQuery = "SELECT f.film_id, f.name,f.description,f.release_date, f.duration, m.mpa_name " +
                "FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Фильм с таким id не обнаружен");
        }
    }

    @Override
    public Genre findGenreById(int id) {
        String sqlQuery = "SELECT * FROM genres where genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (DataAccessException e) {
            throw new GenreNotFoundException("Жанр с таким id не обнаружен");
        }
    }

    @Override
    public Mpa findMpaById(int id) {
        String sqlQuery = "select * from mpa where mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } catch (DataAccessException e) {
            throw new MpaNotFoundException("Рейтинг с таким id не обнаружен");
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "insert into likes(film_id, user_id) values(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);

    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        if (jdbcTemplate.update(sqlQuery, filmId, userId) <= 0) {
            throw new LikeNotFoundException("Лайк не найден");
        }
    }

    @Override
    public List<Film> getTopLikesFilms(int count) {
        String sqlQuery = "SELECT f.film_id, f.NAME, f.DESCRIPTION, f.RELEASE_DATE," +
                " f.DURATION FROM FILMS f " +
                "LEFT outer JOIN likes l ON l.FILM_ID = f.FILM_ID " +
                "GROUP BY f.film_id ORDER BY count(l.user_id) desc LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
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
    public List<Mpa> findMpa() {
        String sqlQuery = "select mpa_id, mpa_name from mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
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
            List<Genre> genres = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate(sqlQueryGenres,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, film.getId());
                            ps.setInt(2, genres.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        findFilmById(film.getId());
        String sqlQuery = "update films set " + "name = ?, description = ?, release_date = ? , duration = ?, " +
                "mpa_id = ? where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
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
        List<Integer> likes = new ArrayList<>(film.getLikes());
        jdbcTemplate.batchUpdate(sqlQueryLikes,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, likes.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return likes.size();
                    }
                });
    }

    private void genresUpdate(Film film) {
        String queryDeleteGenres = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(queryDeleteGenres, film.getId());

        String sqlQueryGenres = "insert into film_genres(film_id,genre_id)" + "values (?, ?)";
        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate(sqlQueryGenres,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, film.getId());
                            ps.setInt(2, genres.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
    }

    private Set<Integer> getLikesForFilm(int id) {
        String sqlQuery = "select user_id from likes where film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToIdLike, id));
    }

    private Set<Genre> getGenresForFilm(int id) {
        String sqlQuery = "select f.genre_id, g.genre_name from film_genres as f " +
                "join genres as g on f.genre_id = g.genre_id where f.film_id = ? order by f.genre_id";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id));
    }

    private Mpa getMpaForFilm(int id) {
        String sqlQuery = "select m.mpa_id, m.mpa_name from films as f " +
                "join mpa as m on f.mpa_id = m.mpa_id where f.film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder().id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date")
                        .toLocalDate()).duration(resultSet.getInt("duration"))
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

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        return mpa;
    }

    private Integer mapRowToIdLike(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }
}