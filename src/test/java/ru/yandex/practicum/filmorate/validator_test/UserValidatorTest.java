package ru.yandex.practicum.filmorate.validator_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    @DisplayName("Электронная почта не может быть пустой")
    @Test
    void shouldThrowValidationExceptionWhenEmailIsEmpty() {
        User nonEmailUser = User.builder()
                .email("")
                .login("login")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(nonEmailUser));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
    }

    @DisplayName("Электронная почта должна содержать символ @")
    @Test
    void shouldThrowValidationExceptionWhenEmailNotContainAt() {
        User user = User.builder()
                .email("mali.ru")
                .login("login")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
    }

    @DisplayName("Логин не может содержать пробелы")
    @Test
    void shouldThrowValidationExceptionWhenLoginContainsSpaces() {
        User user = User.builder()
                .email("mail@mali.ru")
                .login("log in")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());


    }

    @DisplayName("Логин не может быть пустым")
    @Test
    void shouldThrowValidationExceptionWhenLoginIsEmpty() {
        User emptyLoginUser = User.builder()
                .email("mail@mali.ru")
                .login("")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(emptyLoginUser));
        assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());
    }


    @DisplayName("Дата рождения не может быть в будущем")
    @Test
    void shouldThrowValidationExceptionWhenBirthdayInFuture() {
        User futureUser = User.builder()
                .email("mail@mali.ru")
                .login("login")
                .birthday(LocalDate.of(19999, 9, 9))
                .build();
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(futureUser));
        assertEquals("Дата рождения не может быть в будущем", e.getMessage());
    }

    @DisplayName("Пользователь без имени в качестве имя должен получает логин")
    @Test
    void userWithoutNameSetNameLogin() {
        User namelessUser = User.builder()
                .email("mail@mali.ru")
                .login("login")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        assertNull(namelessUser.getName());
        UserValidator.checkUser(namelessUser);
        assertEquals("login", namelessUser.getName());
    }

    @DisplayName("Обновление несуществующего пользователя должно выбросить исключение")
    @Test
    void shouldThrowValidationExceptionWhenUpdateNonExistUser() {
        List<User> users = new ArrayList<>();
        User user = User.builder()
                .email("mail@mali.ru")
                .login("login")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        user.setId(1);
        users.add(user);
        User updateUser = User.builder()
                .email("mail@mali.ru")
                .login("login")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        updateUser.setId(9999);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> UserValidator.checkId(users, updateUser));
        assertEquals("Пользователя с id:" + updateUser.getId() + " не существует", e.getMessage());
    }
}
