package apiClients.auth;

import models.logout.EmptyRefreshTokenLogoutBodyModel;
import models.logout.EmptyRefreshTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.ReusedRefreshTokenLogoutResponseModel;
import static io.restassured.RestAssured.given;
import static specs.logout.LogoutSpec.*;

public class LogoutApiClient {


    public void logout(String refreshToken) {
        LogoutBodyModel body = new LogoutBodyModel(refreshToken);

        given(logoutRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
    }

    public ReusedRefreshTokenLogoutResponseModel logoutWithReusedToken(String refreshToken) {
        LogoutBodyModel body = new LogoutBodyModel(refreshToken);

        return given(logoutRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(ReusedRefreshTokenLogoutResponseSpec)
                .extract()
                .as(ReusedRefreshTokenLogoutResponseModel.class);
    }

    public EmptyRefreshTokenLogoutResponseModel logoutWithoutToken() {
        EmptyRefreshTokenLogoutBodyModel body = new EmptyRefreshTokenLogoutBodyModel();

        return given(logoutRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyRefreshTokenLogoutResponseSpec)
                .extract()
                .as(EmptyRefreshTokenLogoutResponseModel.class);
    }

    public ReusedRefreshTokenLogoutResponseModel logoutWithAccessToken(String accessToken) {
        LogoutBodyModel body = new LogoutBodyModel(accessToken);

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
