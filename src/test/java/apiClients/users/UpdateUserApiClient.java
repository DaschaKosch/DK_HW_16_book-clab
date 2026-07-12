package apiClients.users;

import models.update_user.*;
import static io.restassured.RestAssured.given;
import static specs.user.UpdateUserSpec.*;

public class UpdateUserApiClient {

        public SuccessfulUpdateUserPutResponseModel updateUserWithPut(String accessToken,
                                                                      String username,
                                                                      String firstName,
                                                                      String lastName,
                                                                      String email) {
            UpdateUserBodyModel body = new UpdateUserBodyModel(username, firstName, lastName, email);

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

        public SuccessfulUpdateUserPatchResponseModel updateUserWithPatch(String accessToken,
                                                                          String username,
                                                                          String firstName,
                                                                          String lastName,
                                                                          String email) {
            UpdateUserBodyModel body = new UpdateUserBodyModel(username, firstName, lastName, email);

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

        public SuccessfulUpdateUserPatchResponseModel partialUpdateUser(String accessToken, String username) {
            PartialUpdateUserBodyModel body = new PartialUpdateUserBodyModel(username);

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


        public InvalidPartialUpdateUserResponseBodyModel partialUpdateWithPut(String accessToken, String username) {
            PartialUpdateUserBodyModel body = new PartialUpdateUserBodyModel(username);

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
