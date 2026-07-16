package apiClients.users;

import io.qameta.allure.Step;
import models.registration.*;
import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.*;

public class RegistrationApiClient {

    @Step("Отправка POST-запроса на /users/register/ и проверка HTTP-статуса 201")
    public SuccessfulRegistrationResponseModel registerUser(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Отправка POST-запроса на /users/register/ с существующим пользователем и проверка HTTP-статуса 400")
    public ExistingUserResponseRecordsModel registerExistingUser(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongExistingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseRecordsModel.class);
    }

    @Step("Отправка POST-запроса на /users/register/ с пустым логином и проверка HTTP-статуса 400")
    public EmptyUsernameResponseModel registerWithEmptyUsername(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyUsernameRegistrationResponseSpec)
                .extract()
                .as(EmptyUsernameResponseModel.class);
    }

    @Step("Отправка POST-запроса на /users/register/ с пустым паролем и проверка HTTP-статуса 400")
    public EmptyPasswordResponseModel registerWithEmptyPassword(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyPasswordRegistrationResponseSpec)
                .extract()
                .as(EmptyPasswordResponseModel.class);
    }

    @Step("Отправка POST-запроса на /users/register/ с некорректным логином и проверка HTTP-статуса 400")
    public WrongUsernameResponseModel registerWithWrongUsername(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameResponseModel.class);
    }

    @Step("Отправка POST-запроса на /users/register/ с неверным Content-Type и проверка HTTP-статуса 415")
    public UnsupportedMediaTypeRegistrationBodyModel registerWithUnsupportedMediaType(RegistrationBodyModel body) {
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

