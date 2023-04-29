package ru.yandex.practicum.filmorate.validator_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    private ValidationException e;

    @DisplayName("Электронная почта не может быть пустой и должна содержать символ @")
    @Test
    void shouldThrowValidationExceptionWhenEmailIsEmpty() {
        User nonEmailUser = new User("", "login", LocalDate.of(1999, 9, 9));
        e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(nonEmailUser));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());

        User user = new User("mail.ru", "login", LocalDate.of(1999, 9, 9));
        e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
    }

    @DisplayName("Логин не может быть пустым и содержать пробелы")
    @Test
    void shouldThrowValidationExceptionWhenLoginIsEmptyOrContainsSpaces() {
        User user = new User("mail@mali.ru", "login login", LocalDate.of(1999, 9, 9));
        e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());

        User emptyLoginUser = new User("mail@mali.ru", "", LocalDate.of(1999, 9, 9));
        e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(emptyLoginUser));
        assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());
    }

    @DisplayName("Дата рождения не может быть в будущем")
    @Test
    void shouldThrowValidationExceptionWhenBirthdayInFuture() {
        User futureUser = new User("mail@mali.ru", "login", LocalDate.of(9999, 9, 9));
        e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkUser(futureUser));
        assertEquals("Дата рождения не может быть в будущем", e.getMessage());
    }

    @DisplayName("Пользователь без имени в качестве имя должен получает логин")
    @Test
    void userWithoutNameSetNameLogin() {
        User namelessUser = new User("mail@mali.ru", "login", LocalDate.of(1999, 9, 9));
        assertNull(namelessUser.getName());
        UserValidator.checkUser(namelessUser);
        assertEquals("login", namelessUser.getName());
    }

    @DisplayName("Обновление несуществующего пользователя должно выбросить исключение")
    @Test
    void shouldThrowValidationExceptionWhenUpdateNonExistUser() {
        Map<Integer, User> users = new HashMap<>();
        User user = new User("mail@mali.ru", "login", LocalDate.of(1999, 9, 9));
        user.setId(1);
        users.put(user.getId(), user);
        User updateUser = new User("mail@mali.ru", "login", LocalDate.of(1999, 9, 9));
        updateUser.setId(9999);

        e = assertThrows(
                ValidationException.class,
                () -> UserValidator.checkId(users, updateUser));
        assertEquals("Обновить пользователя с id:" + updateUser.getId() + " невозможно по причине его отсутствия", e.getMessage());
    }
}
