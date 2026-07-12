package apiClients.auth;

import models.login.*;
import models.registration.RegistrationBodyModel;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class LoginApiClient {

    public SuccessfulLoginResponseRecordsModel login(String username, String password) {
        LoginBodyRecordsModel body = new LoginBodyRecordsModel(username, password);

        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseRecordsModel.class);
    }

    public WrongCredentialsLoginResponseRecordsModel loginWithWrongPassword(String username, String wrongPassword) {
        LoginBodyRecordsModel body = new LoginBodyRecordsModel(username, wrongPassword);

        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialResponseSpecification)
                .extract()
                .as(WrongCredentialsLoginResponseRecordsModel.class);
    }

    public EmptyRefreshTokenLoginResponseModel refreshWithoutToken() {
        EmptyRefreshTokenLoginBodyModel body = new EmptyRefreshTokenLoginBodyModel();

        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/refresh/")
                .then()
                .spec(emptyRefreshTokenResponseSpec)
                .extract()
                .as(EmptyRefreshTokenLoginResponseModel.class);
    }

    public WrongRefreshTokenLoginResponseModel refreshWithInvalidToken(String invalidToken) {
        WrongRefreshTokenLoginBodyModel body = new WrongRefreshTokenLoginBodyModel(invalidToken);

        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/refresh/")
                .then()
                .spec(wrongRefreshTokenResponseSpec)
                .extract()
                .as(WrongRefreshTokenLoginResponseModel.class);
    }

    /**
     * Вспомогательный метод: регистрация + логин, возвращает access-токен
     * Используется в других тестах как предусловие
     */
    public String registerAndLogin(String username, String password) {
        // ШАГ 1: Регистрация
        step("Регистрация пользователя", () -> {
            RegistrationBodyModel body = new RegistrationBodyModel(username, password);
            given(registrationRequestSpec)
                    .body(body)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        // ШАГ 2: Логин и возврат access-токена
        return step("Авторизация пользователя и получение access-токена", () -> {
            LoginBodyRecordsModel body = new LoginBodyRecordsModel(username, password);
            return given(loginRequestSpec)
                    .body(body)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .as(SuccessfulLoginResponseRecordsModel.class)
                    .access();
        });
    }

    /**
     * Вспомогательный метод: регистрация + логин, возвращает refresh-токен
     * Используется в других тестах как предусловие
     */
    public String registerAndLoginForRefresh(String username, String password) {
        // ШАГ 1: Регистрация
        step("Регистрация пользователя", () -> {
            RegistrationBodyModel body = new RegistrationBodyModel(username, password);
            given(registrationRequestSpec)
                    .body(body)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        // ШАГ 2: Логин и возврат refresh-токена
        return step("Авторизация пользователя и получение refresh-токена", () -> {
            LoginBodyRecordsModel body = new LoginBodyRecordsModel(username, password);
            return given(loginRequestSpec)
                    .body(body)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .as(SuccessfulLoginResponseRecordsModel.class)
                    .refresh();
        });
    }
}


