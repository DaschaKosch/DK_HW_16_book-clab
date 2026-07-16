package tests;

import models.login.*;
import models.registration.RegistrationBodyModel;
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

        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);

        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        SuccessfulLoginResponseRecordsModel response = api.login.login(loginBody);

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

        LoginBodyRecordsModel body = new LoginBodyRecordsModel(td.username, td.wrongPassword);
        WrongCredentialsLoginResponseRecordsModel response = api.login.loginWithWrongPassword(body);

        step("Проверка бизнес-логики: валидация ошибки неверных учетных данных", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_OR_PASSWORD);
        });
    }

    @DisplayName("Обновление токена без refresh: негативный тест")
    @Test
    public void emptyRefreshTokenLoginTest() {

        EmptyRefreshTokenLoginBodyModel body = new EmptyRefreshTokenLoginBodyModel();
        EmptyRefreshTokenLoginResponseModel response = api.login.refreshWithoutToken(body);

        step("Проверка бизнес-логики: валидация ошибки отсутствия refresh-токена", () -> {
            assertThat(response.refresh().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @DisplayName("Обновление токена с невалидным refresh: негативный тест")
    @Test
    public void wrongRefreshTokenLoginTest() {

        WrongRefreshTokenLoginBodyModel body = new WrongRefreshTokenLoginBodyModel(td.invalidToken);
        WrongRefreshTokenLoginResponseModel response = api.login.refreshWithInvalidToken(body);

        step("Проверка бизнес-логики: валидация ошибки невалидного refresh-токена", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_VALID_TOKEN);
            assertThat(response.code()).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

}
