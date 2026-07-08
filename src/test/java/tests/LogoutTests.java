package tests;


import models.login.LoginBodyRecordsModel;
import models.logout.EmptyRefreshTokenLogoutBodyModel;
import models.logout.EmptyRefreshTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.ReusedRefreshTokenLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import static io.qameta.allure.Allure.step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    TestData td = new TestData();


    @DisplayName("Успешный logout по валидному refresh-токену")
    @Test
    public void successfulLogoutTest() {
        step("Зарегистрировать пользователя", () -> {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
        given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);
        });
        String refreshToken = step("Авторизоваться и получить refresh token", () -> {
        LoginBodyRecordsModel data = new LoginBodyRecordsModel(td.username, td.password);
            return given(loginRequestSpec)
                .body(data)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec).extract().path("refresh");
        });

        step("Выполнить logout с refresh token", () -> {
        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
        });

    }

    @DisplayName("Повторное использование refresh-токена при logout (401 Token is blacklisted): негативный тест")
    @Test
    public void logoutWithReusedTokenTest() {
        step("Зарегистрировать пользователя", () -> {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
        given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);
        });

        String refreshToken = step("Авторизоваться и получить refresh token", () -> {
        LoginBodyRecordsModel data = new LoginBodyRecordsModel(td.username, td.password);
        return given(loginRequestSpec)
                .body(data)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("refresh");
        });

        step("Выполнить logout с refresh token", () -> {
        LogoutBodyModel logoutFirstData = new LogoutBodyModel(refreshToken);
        given(logoutRequestSpec)
                .body(logoutFirstData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);

        LogoutBodyModel logoutSecondData = new LogoutBodyModel(refreshToken);
        ReusedRefreshTokenLogoutResponseModel logoutResponse =
                given(logoutRequestSpec)
                        .body(logoutSecondData)
                        .when()
                        .post("/auth/logout/")
                        .then()
                        .spec(ReusedRefreshTokenLogoutResponseSpec)
                        .extract().as(ReusedRefreshTokenLogoutResponseModel.class);

        String actualDetailReusedRefreshToken = logoutResponse.detail();
        String actualCodeReusedRefreshToken = logoutResponse.code();
        assertThat(actualDetailReusedRefreshToken).isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);
        assertThat(actualCodeReusedRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });

    }

    @DisplayName("Logout без refresh-токена (ошибка валидации): негативный тест ")
    @Test
    public void emptyRefreshTokenTest() {
        step("logout без refresh token", () -> {
        EmptyRefreshTokenLogoutBodyModel logoutData = new EmptyRefreshTokenLogoutBodyModel();
        EmptyRefreshTokenLogoutResponseModel logoutResponse =
                given(logoutRequestSpec)
                        .body(logoutData)
                        .when()
                        .post("/auth/logout/")
                        .then()
                        .spec(emptyRefreshTokenLogoutResponseSpec)
                        .extract().as(EmptyRefreshTokenLogoutResponseModel.class);


        String actualErrorWithoutRefreshToken = logoutResponse.refresh().get(0);
        assertThat(actualErrorWithoutRefreshToken).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @DisplayName("Logout с access-токеном вместо refresh (401 Token has wrong type): негативный тест")
    @Test
    public void accessTokenInsteadOfRefreshTokenTest() {
        step("Зарегистрировать пользователя", () -> {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
        given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);
        });
        String accessToken = step("Авторизоваться и получить access token", () -> {
        LoginBodyRecordsModel data = new LoginBodyRecordsModel(td.username, td.password);
            return given(loginRequestSpec)
                .body(data)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("access");
        });

        step("Проверить ошибку с access token вместо refresh token", () -> {
        LogoutBodyModel logoutData = new LogoutBodyModel(accessToken);
        ReusedRefreshTokenLogoutResponseModel logoutResponse =
                given(logoutRequestSpec)
                        .body(logoutData)
                        .when()
                        .post("/auth/logout/")
                        .then()
                        .spec(ReusedRefreshTokenLogoutResponseSpec)
                        .extract().as(ReusedRefreshTokenLogoutResponseModel.class);

        String actualDetailReusedRefreshToken = logoutResponse.detail();
        String actualCodeReusedRefreshToken = logoutResponse.code();
        assertThat(actualDetailReusedRefreshToken).isEqualTo(EXPECTED_ERROR_WRONG_TOKEN_TYPE);
        assertThat(actualCodeReusedRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });

    }

}
