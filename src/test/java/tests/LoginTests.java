package tests;

import models.login.*;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    TestData td = new TestData();

    @DisplayName("Успешная авторизация пользователя")
    @Test
    public void successfulLoginTest() {

        step("Зарегистрировать пользователя", () -> {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);

                 given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);
        });

        SuccessfulLoginResponseRecordsModel loginResponse = step("Отправка POST-запроса на /auth/token/ и проверка HTTP-статуса 200", () -> {
            LoginBodyRecordsModel loginData = new LoginBodyRecordsModel(td.username, td.password);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().as(SuccessfulLoginResponseRecordsModel.class);
        });

        step("Проверка бизнес-логики: валидация access и refresh токенов", () -> {
            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).isNotEmpty();
            assertThat(actualRefresh).isNotEmpty();
            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });

    }

    @DisplayName("Неуспешная авторизация пользователя (неверный пароль): негативный тест")
    @Test
    public void wrongCredentialsLoginTest() {

        WrongCredentialsLoginResponseRecordsModel loginResponse = step("Отправка POST-запроса на /auth/token/ с неверным паролем и проверка HTTP-статуса 401", () -> {
            LoginBodyRecordsModel loginData = new LoginBodyRecordsModel(td.username, td.wrongPassword);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialResponseSpecification)
                    .extract().as(WrongCredentialsLoginResponseRecordsModel.class);
        });

        // ШАГ 2: Проверка бизнес-логики (текст ошибки)
        step("Проверка бизнес-логики: валидация ошибки неверных учетных данных", () -> {
            assertThat(loginResponse.detail()).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_OR_PASSWORD);
        });

    }

    @DisplayName("Обновление токена без поля refresh (400 Bad Request): негативный тест")
    @Test
    public void emptyRefreshTokenLoginTest() {

        EmptyRefreshTokenLoginResponseModel emptyRefreshTokenResponseModel = step("Отправка POST-запроса на /auth/token/refresh/ без refresh-токена и проверка HTTP-статуса 400", () -> {
            EmptyRefreshTokenLoginBodyModel emptyRefreshToken = new EmptyRefreshTokenLoginBodyModel();
            return given(loginRequestSpec)
                    .body(emptyRefreshToken)
                    .when()
                    .post("/auth/token/refresh/")
                    .then()
                    .spec(emptyRefreshTokenResponseSpec)
                    .extract().as(EmptyRefreshTokenLoginResponseModel.class);
        });

        step("Проверка бизнес-логики: валидация ошибки отсутствия refresh-токена", () -> {
            assertThat(emptyRefreshTokenResponseModel.refresh().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @DisplayName("Обновление токена с невалидным refresh (401 Unauthorized): негативный тест")
    @Test
    public void wrongRefreshTokenLoginTest() {

        WrongRefreshTokenLoginResponseModel loginResponse = step("Отправка POST-запроса на /auth/token/refresh/ с невалидным токеном и проверка HTTP-статуса 401", () -> {
            WrongRefreshTokenLoginBodyModel wrongRefreshTokenBodyModel = new WrongRefreshTokenLoginBodyModel(td.invalidToken);
            return given(loginRequestSpec)
                    .body(wrongRefreshTokenBodyModel)
                    .when()
                    .post("/auth/token/refresh/")
                    .then()
                    .spec(wrongRefreshTokenResponseSpec)
                    .extract().as(WrongRefreshTokenLoginResponseModel.class);
        });


        step("Проверка бизнес-логики: валидация ошибки невалидного refresh-токена", () -> {
            assertThat(loginResponse.detail()).isEqualTo(EXPECTED_ERROR_VALID_TOKEN);
            assertThat(loginResponse.code()).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });

    }

}
