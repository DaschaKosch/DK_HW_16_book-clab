package apiClients.users;

import io.qameta.allure.Step;
import models.update_user.*;
import static io.restassured.RestAssured.given;
import static specs.user.UpdateUserSpec.*;

public class UpdateUserApiClient {

    @Step("Отправка PUT-запроса на /users/me/ и проверка HTTP-статуса 200")
    public SuccessfulUpdateUserPutResponseModel updateUserWithPut(String accessToken, UpdateUserBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserPutResponseModel.class);
    }

    @Step("Отправка PATCH-запроса на /users/me/ и проверка HTTP-статуса 200")
    public SuccessfulUpdateUserPatchResponseModel updateUserWithPatch(String accessToken, UpdateUserBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserPatchResponseModel.class);
    }

    @Step("Отправка PATCH-запроса на /users/me/ с частичным обновлением и проверка HTTP-статуса 200")
    public SuccessfulUpdateUserPatchResponseModel partialUpdateUser(String accessToken, PartialUpdateUserBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserPatchResponseModel.class);
    }

    @Step("Отправка PUT-запроса на /users/me/ с неполными данными и проверка HTTP-статуса 400")
    public InvalidPartialUpdateUserResponseBodyModel partialUpdateWithPut(String accessToken, PartialUpdateUserBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(invalidPartialUpdateUserResponseSpec)
                .extract()
                .as(InvalidPartialUpdateUserResponseBodyModel.class);
    }
}
