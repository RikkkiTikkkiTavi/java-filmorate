package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    User testUser;
    Film testFilm;
    List<User> users;
    List<Film> films;
    Mpa mpa;

    @BeforeEach
    public void initEach() {
        mpa = new Mpa();
        mpa.setId(1);

        testUser = User.builder()
                .name("name")
                .login("login")
                .email("mail@mail.ru")
                .birthday(LocalDate.now())
                .friends(new HashSet<>())
                .build();

        testFilm = Film.builder()
                .name("name")
                .description("des")
                .duration(200)
                .releaseDate(LocalDate.now())
                .likes(new HashSet<>())
                .mpa(mpa)
                .genres(new HashSet<>())
                .build();

        users = new ArrayList<>();
        films = new ArrayList<>();
    }

    @DisplayName("Создание пользователя увеличивает размер списка пользователей на 1")
    @Test
    public void createUserMustIncreaseUserListBy1() {
        assertEquals(userStorage.findAll().size(), 0);
        userStorage.create(testUser);
        assertEquals(userStorage.findAll().size(), 1);
    }

    @DisplayName("При создании пользователя, id генерируется автоматически")
    @Test
    public void createUserMustGeneratedId() {
        assertEquals(testUser.getId(), 0);
        assertEquals(userStorage.findAll().get(0).getId(), 1);
    }

    @DisplayName("Метод findAll() возвращает список пользователей ")
    @Test
    public void findAllReturnUserList() {
        assertNotEquals(userStorage.findAll(), users);
        users.add(userStorage.findAll().get(0));
        assertEquals(userStorage.findAll(), users);
    }

    @DisplayName("Метод update() обновляет данные пользователя и возвращает обновленный объект User")
    @Test
    public void updateReturnUpdatedUser() {
        User updateUser = User.builder()
                .id(userStorage.findAll().get(0).getId())
                .name("newName")
                .login("newLogin")
                .email("mail@mail.ru")
                .birthday(LocalDate.now())
                .friends(new HashSet<>())
                .build();
        assertEquals(userStorage.update(updateUser), updateUser);
        assertEquals(userStorage.findAll().get(0), updateUser);
    }

    @DisplayName("Создание фильма увеличивает размер списка фильмов на 1")
    @Test
    public void createFilmMustIncreaseFilmListBy1() {
        assertEquals(filmStorage.findAll().size(), 0);
        filmStorage.create(testFilm);
        assertEquals(filmStorage.findAll().size(), 1);
    }

    @DisplayName("При создании фильма, id генерируется автоматически")
    @Test
    public void createFilmMustGeneratedId() {
        assertEquals(testFilm.getId(), 0);
        assertEquals(filmStorage.findAll().get(0).getId(), 1);
    }

    @DisplayName("Метод update() обновляет данные фильма и возвращает обновленный объект Film")
    @Test
    public void updateReturnUpdatedFilm() {
        mpa.setId(2);
        Film updateFilm = Film.builder()
                .id(filmStorage.findAll().get(0).getId())
                .name("newName")
                .description("newDes")
                .duration(300)
                .releaseDate(LocalDate.now())
                .likes(new HashSet<>())
                .mpa(mpa)
                .build();
        assertEquals(filmStorage.update(updateFilm), updateFilm);
        assertEquals(filmStorage.findAll().get(0), updateFilm);
    }

    @DisplayName("Метод findGenres() возвращает список всех жанров")
    @Test
    public void findGenresReturnAllGenres() {
        assertEquals(filmStorage.findGenres().size(), 6);
        assertEquals(filmStorage.findGenres().get(0).getName(), "Комедия");
        assertEquals(filmStorage.findGenres().get(0).getId(), 1);
        assertEquals(filmStorage.findGenres().get(5).getName(), "Боевик");
        assertEquals(filmStorage.findGenres().get(5).getId(), 6);
    }

    @DisplayName("Метод findMpa() возвращает список всех рейтингов")
    @Test
    public void findMpaReturnAllGenres() {
        assertEquals(filmStorage.findMpa().size(), 5);
        assertEquals(filmStorage.findMpa().get(0).getName(), "G");
        assertEquals(filmStorage.findMpa().get(0).getId(), 1);
        assertEquals(filmStorage.findMpa().get(4).getName(), "NC-17");
        assertEquals(filmStorage.findMpa().get(4).getId(), 5);
    }
}