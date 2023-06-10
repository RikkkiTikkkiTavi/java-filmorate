package ru.yandex.practicum.filmorate.validator_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    User user;

    @BeforeEach
    void initEach() {
        user = User.builder()
                .email("mail@mail.ru")
                .login("login")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
    }

    @DisplayName("Электронная почта не может быть пустой")
    @Test
    void shouldThrowValidationExceptionWhenEmailIsEmpty() {
        user.setEmail("");
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
    }

    @DisplayName("Электронная почта должна содержать символ @")
    @Test
    void shouldThrowValidationExceptionWhenEmailNotContainAt() {
        user.setEmail("mailmail");
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
    }

    @DisplayName("Логин не может содержать пробелы")
    @Test
    void shouldThrowValidationExceptionWhenLoginContainsSpaces() {
        user.setLogin("name name");
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());


    }

    @DisplayName("Логин не может быть пустым")
    @Test
    void shouldThrowValidationExceptionWhenLoginIsEmpty() {
        user.setLogin("");
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());
    }


    @DisplayName("Дата рождения не может быть в будущем")
    @Test
    void shouldThrowValidationExceptionWhenBirthdayInFuture() {
        user.setBirthday(LocalDate.of(19999, 9, 9));
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Дата рождения не может быть в будущем", e.getMessage());
    }

    @DisplayName("Пользователь без имени в качестве имя должен получает логин")
    @Test
    void userWithoutNameSetNameLogin() {
        user.setName(null);
        assertNull(user.getName());
        UserValidator.checkUser(user);
        assertEquals("login", user.getName());
    }
}
