package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    TestData td = new TestData();

    @DisplayName("Успешная авторизация пользователя")
    @Test
    public void successfulLoginTest() {

        step("Отправка POST-запроса на /users/register/ и проверка HTTP-статуса 201", () -> {
            api.registration.registerUser(td.username, td.password);
        });

        SuccessfulLoginResponseRecordsModel response = step(
                "Отправка POST-запроса на /auth/token/ и проверка HTTP-статуса 200",
                () -> api.login.login(td.username, td.password)
        );

        step("Проверка бизнес-логики: валидация access и refresh токенов", () -> {
            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            assertThat(response.access()).isNotEmpty();
            assertThat(response.refresh()).isNotEmpty();
            assertThat(response.access()).startsWith(expectedTokenPath);
            assertThat(response.refresh()).startsWith(expectedTokenPath);
            assertThat(response.access()).isNotEqualTo(response.refresh());
        });
    }

    @DisplayName("Неуспешная авторизация (неверный пароль): негативный тест")
    @Test
    public void wrongCredentialsLoginTest() {

        WrongCredentialsLoginResponseRecordsModel response = step(
                "Отправка POST-запроса на /auth/token/ с неверным паролем и проверка HTTP-статуса 401",
                () -> api.login.loginWithWrongPassword(td.username, td.wrongPassword)
        );

        step("Проверка бизнес-логики: валидация ошибки неверных учетных данных", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_OR_PASSWORD);
        });
    }

    @DisplayName("Обновление токена без refresh: негативный тест")
    @Test
    public void emptyRefreshTokenLoginTest() {

        EmptyRefreshTokenLoginResponseModel response = step(
                "Отправка POST-запроса на /auth/token/refresh/ без refresh-токена и проверка HTTP-статуса 400",
                () -> api.login.refreshWithoutToken()
        );

        step("Проверка бизнес-логики: валидация ошибки отсутствия refresh-токена", () -> {
            assertThat(response.refresh().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @DisplayName("Обновление токена с невалидным refresh: негативный тест")
    @Test
    public void wrongRefreshTokenLoginTest() {

        WrongRefreshTokenLoginResponseModel response = step(
                "Отправка POST-запроса на /auth/token/refresh/ с невалидным токеном и проверка HTTP-статуса 401",
                () -> api.login.refreshWithInvalidToken(td.invalidToken)
        );

        step("Проверка бизнес-логики: валидация ошибки невалидного refresh-токена", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_VALID_TOKEN);
            assertThat(response.code()).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

}
