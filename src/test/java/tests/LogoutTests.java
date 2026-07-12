package tests;

import models.logout.EmptyRefreshTokenLogoutResponseModel;
import models.logout.ReusedRefreshTokenLogoutResponseModel;
import static io.qameta.allure.Allure.step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    TestData td = new TestData();


    @DisplayName("Успешный logout по валидному refresh-токену")
    @Test
    public void successfulLogoutTest() {

        step("Отправка POST-запроса на /users/register/ и проверка HTTP-статуса 201", () -> {
            api.registration.registerUser(td.username, td.password);
        });

        // Получение refresh-токена (с возвратом значения)
        String refreshToken = step("Отправка POST-запроса на /auth/token/ и проверка HTTP-статуса 200",
                () -> api.login.login(td.username, td.password).refresh()
        );

        step("Отправка POST-запроса на /auth/logout/ и проверка HTTP-статуса 200", () -> {
            api.logout.logout(refreshToken);
        });
    }

    @DisplayName("Повторное использование refresh-токена: негативный тест")
    @Test
    public void logoutWithReusedTokenTest() {

        step("Отправка POST-запроса на /users/register/ и проверка HTTP-статуса 201", () -> {
            api.registration.registerUser(td.username, td.password);
        });

        String refreshToken = step("Отправка POST-запроса на /auth/token/ и проверка HTTP-статуса 200",
                () -> api.login.login(td.username, td.password).refresh()
        );

        step("Отправка POST-запроса на /auth/logout/ (первый) и проверка HTTP-статуса 200", () -> {
            api.logout.logout(refreshToken);
        });

        ReusedRefreshTokenLogoutResponseModel response = step(
                "Отправка POST-запроса на /auth/logout/ (повторно) и проверка HTTP-статуса 401",
                () -> api.logout.logoutWithReusedToken(refreshToken)
        );

        step("Проверка бизнес-логики: валидация ошибки повторного использования токена", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);
            assertThat(response.code()).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

    @DisplayName("Logout без refresh-токена: негативный тест")
    @Test
    public void emptyRefreshTokenTest() {

        EmptyRefreshTokenLogoutResponseModel response = step(
                "Отправка POST-запроса на /auth/logout/ без токена и проверка HTTP-статуса 400",
                () -> api.logout.logoutWithoutToken()
        );

        step("Проверка бизнес-логики: валидация ошибки отсутствия refresh-токена", () -> {
            assertThat(response.refresh().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @DisplayName("Logout с access-токеном вместо refresh: негативный тест")
    @Test
    public void accessTokenInsteadOfRefreshTokenTest() {

        step("Отправка POST-запроса на /users/register/ и проверка HTTP-статуса 201", () -> {
            api.registration.registerUser(td.username, td.password);
        });

        String accessToken = step("Отправка POST-запроса на /auth/token/ и проверка HTTP-статуса 200",
                () -> api.login.login(td.username, td.password).access()
        );

        ReusedRefreshTokenLogoutResponseModel response = step(
                "Отправка POST-запроса на /auth/logout/ с access-токеном и проверка HTTP-статуса 401",
                () -> api.logout.logoutWithAccessToken(accessToken)
        );

        step("Проверка бизнес-логики: валидация ошибки неверного типа токена", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_WRONG_TOKEN_TYPE);
            assertThat(response.code()).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

}
