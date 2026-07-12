package apiClients.club;

import io.qameta.allure.Step;
import models.club.ClubRequestModel;
import models.club.ClubResponseModel;
import static io.restassured.RestAssured.given;
import static specs.club.ClubSpec.*;

public class ClubApiClient {
    @Step("Отправка POST-запроса на /clubs/ и проверка HTTP-статуса 201")
    public ClubResponseModel createClub(ClubRequestModel body, String token) {
        return given(clubRequestSpec)
                .body(body)
                .when()
                .auth().oauth2(token)
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Получение информации о клубе")
    public ClubResponseModel getClubById(int clubId, String token) {
        return given(clubRequestSpec)
                .auth().oauth2(token)
                .when()
                .get("/clubs/{id}/", clubId)
                .then()
                .spec(successfulGetClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }
    @Step("Полное обновление клуба: отправка PUT-запроса на /clubs/{id}/ и проверка HTTP-статуса 200")
    public ClubResponseModel updateClub(int clubId, ClubRequestModel body, String token) {
        return given(clubRequestSpec)
                .body(body)
                .when()
                .auth().oauth2(token)
                .put("/clubs/{id}/", clubId)
                .then()
                .spec(successfulUpdateClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);

    }
    @Step("Удаление клуба: отправка DELETE-запроса на /clubs/{id}/ и проверка HTTP-статуса 204")
    public void deleteClub(int clubId, String token) {
        given(clubRequestSpec)
                .auth().oauth2(token)
                .when()
                .delete("/clubs/{id}/", clubId)
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }
}
