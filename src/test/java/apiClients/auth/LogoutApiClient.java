package apiClients.auth;

import io.qameta.allure.Step;
import models.logout.EmptyRefreshTokenLogoutBodyModel;
import models.logout.EmptyRefreshTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.ReusedRefreshTokenLogoutResponseModel;
import static io.restassured.RestAssured.given;
import static specs.logout.LogoutSpec.*;

public class LogoutApiClient {


    @Step("Отправка POST-запроса на /auth/logout/ и проверка HTTP-статуса 200")
    public void logout(LogoutBodyModel body) {
        given(logoutRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
    }

    @Step("Отправка POST-запроса на /auth/logout/ с повторным токеном и проверка HTTP-статуса 401")
    public ReusedRefreshTokenLogoutResponseModel logoutWithReusedToken(LogoutBodyModel body) {
        return given(logoutRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(ReusedRefreshTokenLogoutResponseSpec)
                .extract()
                .as(ReusedRefreshTokenLogoutResponseModel.class);
    }

    @Step("Отправка POST-запроса на /auth/logout/ без токена и проверка HTTP-статуса 400")
    public EmptyRefreshTokenLogoutResponseModel logoutWithoutToken(EmptyRefreshTokenLogoutBodyModel body) {
        return given(logoutRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyRefreshTokenLogoutResponseSpec)
                .extract()
                .as(EmptyRefreshTokenLogoutResponseModel.class);
    }

    @Step("Отправка POST-запроса на /auth/logout/ с access-токеном и проверка HTTP-статуса 401")
    public ReusedRefreshTokenLogoutResponseModel logoutWithAccessToken(LogoutBodyModel body) {
        return given(logoutRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(ReusedRefreshTokenLogoutResponseSpec)
                .extract()
                .as(ReusedRefreshTokenLogoutResponseModel.class);
    }

}
