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

        step("Авторизоваться и проверить access и refresh token", () -> {
         LoginBodyRecordsModel loginData = new LoginBodyRecordsModel(td.username,td.password);

        SuccessfulLoginResponseRecordsModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseRecordsModel.class);

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

        step("Проверить ошибку при авторизации с неверным password", () -> {
        LoginBodyRecordsModel loginData = new LoginBodyRecordsModel(td.username,td.wrongPassword);

        WrongCredentialsLoginResponseRecordsModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialResponseSpecification)
                .extract().as(WrongCredentialsLoginResponseRecordsModel.class);


        String actualDetailError = loginResponse.detail();
        assertThat(actualDetailError).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_OR_PASSWORD);
        });

    }

    @DisplayName("Обновление токена без поля refresh (400 Bad Request): негативный тест")
    @Test
    public void emptyRefreshTokenLoginTest() {
        step("Проверить ошибку при обновлении токена без refresh token", () -> {
        EmptyRefreshTokenLoginBodyModel emptyRefreshToken = new EmptyRefreshTokenLoginBodyModel();
        EmptyRefreshTokenLoginResponseModel emptyRefreshTokenResponseModel = given(loginRequestSpec)
                .body(emptyRefreshToken)
                .when()
                .post("/auth/token/refresh/")
                .then()
                .spec(emptyRefreshTokenResponseSpec)
                .extract().as(EmptyRefreshTokenLoginResponseModel.class);

        String actualRefresh = emptyRefreshTokenResponseModel.refresh().get(0);
        assertThat(actualRefresh).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @DisplayName("Обновление токена с невалидным refresh (401 Unauthorized): негативный тест")
    @Test
    public void wrongRefreshTokenLoginTest() {
        step("Проверить ошибку при обновлении токена с невалидным refresh token", () -> {
        WrongRefreshTokenLoginBodyModel wrongRefreshTokenBodyModel = new WrongRefreshTokenLoginBodyModel(td.invalidToken);
        WrongRefreshTokenLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(wrongRefreshTokenBodyModel)
                .when()
                .post("/auth/token/refresh/")
                .then()
                .spec(wrongRefreshTokenResponseSpec)
                .extract().as(WrongRefreshTokenLoginResponseModel.class);

        String actualDetailInvalidRefreshToken = loginResponse.detail();
        String actualCodeInvalidRefreshToken = loginResponse.code();

        assertThat(actualDetailInvalidRefreshToken).isEqualTo(EXPECTED_ERROR_VALID_TOKEN);
        assertThat(actualCodeInvalidRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

}
