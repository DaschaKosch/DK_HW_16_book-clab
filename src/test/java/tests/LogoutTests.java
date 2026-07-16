package tests;

import models.login.LoginBodyRecordsModel;
import models.logout.EmptyRefreshTokenLogoutBodyModel;
import models.logout.EmptyRefreshTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.ReusedRefreshTokenLogoutResponseModel;
import static io.qameta.allure.Allure.step;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    TestData td = new TestData();


    @DisplayName("Успешный logout по валидному refresh-токену")
    @Test
    public void successfulLogoutTest() {


        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);
        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        String refreshToken = api.login.login(loginBody).refresh();
        LogoutBodyModel logoutBody = new LogoutBodyModel(refreshToken);
        api.logout.logout(logoutBody);
    }

    @DisplayName("Повторное использование refresh-токена: негативный тест")
    @Test
    public void logoutWithReusedTokenTest() {

        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);

        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        String refreshToken = api.login.login(loginBody).refresh();

        LogoutBodyModel firstLogoutBody = new LogoutBodyModel(refreshToken);
        api.logout.logout(firstLogoutBody);

        LogoutBodyModel secondLogoutBody = new LogoutBodyModel(refreshToken);
        ReusedRefreshTokenLogoutResponseModel response = api.logout.logoutWithReusedToken(secondLogoutBody);

        step("Проверка бизнес-логики: валидация ошибки повторного использования токена", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);
            assertThat(response.code()).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

    @DisplayName("Logout без refresh-токена: негативный тест")
    @Test
    public void emptyRefreshTokenTest() {

        EmptyRefreshTokenLogoutBodyModel body = new EmptyRefreshTokenLogoutBodyModel();
        EmptyRefreshTokenLogoutResponseModel response = api.logout.logoutWithoutToken(body);

        step("Проверка бизнес-логики: валидация ошибки отсутствия refresh-токена", () -> {
            assertThat(response.refresh().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @DisplayName("Logout с access-токеном вместо refresh: негативный тест")
    @Test
    public void accessTokenInsteadOfRefreshTokenTest() {

        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);

        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        String accessToken = api.login.login(loginBody).access();

        LogoutBodyModel body = new LogoutBodyModel(accessToken);

        ReusedRefreshTokenLogoutResponseModel response = api.logout.logoutWithAccessToken(body);

        step("Проверка бизнес-логики: валидация ошибки неверного типа токена", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_WRONG_TOKEN_TYPE);
            assertThat(response.code()).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

}
