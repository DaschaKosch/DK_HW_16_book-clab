package apiClients.users;

import models.registration.*;
import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.*;

public class RegistrationApiClient {

    public SuccessfulRegistrationResponseModel registerUser(String username, String password) {
        RegistrationBodyModel body = new RegistrationBodyModel(username, password);

        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }


    public ExistingUserResponseRecordsModel registerExistingUser(String username, String password) {
        RegistrationBodyModel body = new RegistrationBodyModel(username, password);

        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongExistingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseRecordsModel.class);
    }


    public EmptyUsernameResponseModel registerWithEmptyUsername(String password) {
        RegistrationBodyModel body = new RegistrationBodyModel("", password);

        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyUsernameRegistrationResponseSpec)
                .extract()
                .as(EmptyUsernameResponseModel.class);
    }


    public EmptyPasswordResponseModel registerWithEmptyPassword(String username) {
        RegistrationBodyModel body = new RegistrationBodyModel(username, "");

        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyPasswordRegistrationResponseSpec)
                .extract()
                .as(EmptyPasswordResponseModel.class);
    }


    public WrongUsernameResponseModel registerWithWrongUsername(String wrongUsername, String password) {
        RegistrationBodyModel body = new RegistrationBodyModel(wrongUsername, password);

        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameResponseModel.class);
    }

    public UnsupportedMediaTypeRegistrationBodyModel registerWithUnsupportedMediaType(String username, String password) {
        RegistrationBodyModel body = new RegistrationBodyModel(username, password);

        return given(unsupportedMediaTypeRegistrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(unsupportedMediaTypeRegistrationResponseSpec)
                .extract()
                .as(UnsupportedMediaTypeRegistrationBodyModel.class);
    }
}

