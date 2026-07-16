package apiClients.auth;

import io.qameta.allure.Step;
import models.login.*;
import models.registration.RegistrationBodyModel;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class LoginApiClient {

    @Step("Отправка POST-запроса на /auth/token/ и проверка HTTP-статуса 200")
    public SuccessfulLoginResponseRecordsModel login(LoginBodyRecordsModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseRecordsModel.class);
    }

    @Step("Отправка POST-запроса на /auth/token/ с неверным паролем и проверка HTTP-статуса 401")
    public WrongCredentialsLoginResponseRecordsModel loginWithWrongPassword(LoginBodyRecordsModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialResponseSpecification)
                .extract()
                .as(WrongCredentialsLoginResponseRecordsModel.class);
    }

    @Step("Отправка POST-запроса на /auth/token/refresh/ без refresh-токена и проверка HTTP-статуса 400")
    public EmptyRefreshTokenLoginResponseModel refreshWithoutToken(EmptyRefreshTokenLoginBodyModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/refresh/")
                .then()
                .spec(emptyRefreshTokenResponseSpec)
                .extract()
                .as(EmptyRefreshTokenLoginResponseModel.class);
    }

    @Step("Отправка POST-запроса на /auth/token/refresh/ с невалидным токеном и проверка HTTP-статуса 401")
    public WrongRefreshTokenLoginResponseModel refreshWithInvalidToken(WrongRefreshTokenLoginBodyModel body) {
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
     * Вспомогательный метод: регистрация + логин, возвращает access-токен.
     *  готовые модели — сборка на уровне теста.
     */
    public String registerAndLogin(RegistrationBodyModel registrationBody, LoginBodyRecordsModel loginBody) {
        step("Регистрация пользователя", () -> {
            given(registrationRequestSpec)
                    .body(registrationBody)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        return step("Авторизация пользователя и получение access-токена", () -> {
            return given(loginRequestSpec)
                    .body(loginBody)
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
     * Вспомогательный метод: регистрация + логин, возвращает refresh-токен.
     */
    public String registerAndLoginForRefresh(RegistrationBodyModel registrationBody, LoginBodyRecordsModel loginBody) {
        step("Регистрация пользователя", () -> {
            given(registrationRequestSpec)
                    .body(registrationBody)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        return step("Авторизация пользователя и получение refresh-токена", () -> {
            return given(loginRequestSpec)
                    .body(loginBody)
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


