package tests;

import models.login.LoginBodyRecordsModel;
import models.login.SuccessfulLoginResponseRecordsModel;
import models.registration.RegistrationBodyModel;
import models.update_user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.user.UpdateUserSpec.*;
import static tests.TestData.EXPECTED_REQUIRED_FIELD;

public class UpdateUserTests extends TestBase {

    TestData td = new TestData();

    @DisplayName("Успешное полное обновление пользователя методом put")
    @Test
    public void successfulPutUpdateUserTest() {

        String accessToken = registerAndLogin();

        UpdateUserBodyModel dataUpdateUser = new UpdateUserBodyModel(td.username, td.firstName,
                td.lastName, td.email);
        SuccessfulUpdateUserPutResponseModel responseUpdateUser = given(updateUserRequestSpec)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(dataUpdateUser)
                        .when()
                        .put("/users/me/")
                        .then()
                        .spec(successfulUpdateUserResponseSpec)
                        .extract().as(SuccessfulUpdateUserPutResponseModel.class);

        String actualUsername = responseUpdateUser.username();
        String actualFirstName = responseUpdateUser.firstName();
        String actualLastName = responseUpdateUser.lastName();
        String actualEmail = responseUpdateUser.email();

        assertThat(responseUpdateUser.id()).isPositive();
        assertThat(actualUsername).isEqualTo(td.username);
        assertThat(actualFirstName).isEqualTo(td.firstName);
        assertThat(actualLastName).isEqualTo(td.lastName);
        assertThat(actualEmail).isEqualTo(td.email);
        assertThat(responseUpdateUser.remoteAddr()).isNotBlank();
    }

    @DisplayName("Успешное полное обновление пользователя методом patch")
    @Test
    public void successfulPatchUpdateUserTest() {

        String accessToken = registerAndLogin();

        UpdateUserBodyModel dataUpdateUser = new UpdateUserBodyModel(td.username, td.firstName, td.lastName, td.email);
        SuccessfulUpdateUserPatchResponseModel responseUpdateUser = given(updateUserRequestSpec)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(dataUpdateUser)
                        .when()
                        .patch("/users/me/")
                        .then()
                        .spec(successfulUpdateUserResponseSpec)
                        .extract().as(SuccessfulUpdateUserPatchResponseModel .class);

        String actualUsername = responseUpdateUser.username();
        String actualFirstName = responseUpdateUser.firstName();
        String actualLastName = responseUpdateUser.lastName();
        String actualEmail = responseUpdateUser.email();

        assertThat(responseUpdateUser.id()).isPositive();
        assertThat(actualUsername).isEqualTo(td.username);
        assertThat(actualFirstName).isEqualTo(td.firstName);
        assertThat(actualLastName).isEqualTo(td.lastName);
        assertThat(actualEmail).isEqualTo(td.email);
        assertThat(responseUpdateUser.remoteAddr()).isNotBlank();
    }

    @DisplayName("Успешное частичное обновление пользователя")
    @Test
    public void successfulPartialPatchUpdateUserTest() {

        String accessToken = registerAndLogin();

        PartialUpdateUserBodyModel dataUpdateUser = new PartialUpdateUserBodyModel(td.username);

        SuccessfulUpdateUserPatchResponseModel responseUpdateUser = given(updateUserRequestSpec)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(dataUpdateUser)
                        .when()
                        .patch("/users/me/")
                        .then()
                        .spec(successfulUpdateUserResponseSpec)
                        .extract().as(SuccessfulUpdateUserPatchResponseModel.class);

        String actualUsername = responseUpdateUser.username();

        assertThat(responseUpdateUser.id()).isPositive();
        assertThat(actualUsername).isEqualTo(td.username);

    }

    @DisplayName("Частичное обновление пользователя методом put: негативный тест")
    @Test
    public void partialPutUpdateUserTest() {

        String accessToken = registerAndLogin();

        PartialUpdateUserBodyModel dataUpdateUser = new PartialUpdateUserBodyModel(td.username);

        InvalidPartialUpdateUserResponseBodyModel responseUpdateUser = given(updateUserRequestSpec)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(dataUpdateUser)
                        .when()
                        .put("/users/me/")
                        .then()
                        .spec(invalidPartialUpdateUserResponseSpec)
                        .extract().as(InvalidPartialUpdateUserResponseBodyModel.class);

        String actualFirstName = responseUpdateUser.firstName().get(0);
        String actualLastName = responseUpdateUser.lastName().get(0);
        String actualEmail = responseUpdateUser.email().get(0);

        assertThat(actualFirstName).isEqualTo(EXPECTED_REQUIRED_FIELD);
        assertThat(actualLastName).isEqualTo(EXPECTED_REQUIRED_FIELD);
        assertThat(actualEmail).isEqualTo(EXPECTED_REQUIRED_FIELD);
        assertThat(responseUpdateUser.username()).isNull();
    }

    private String registerAndLogin() {
        // Регистрация
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
        given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);

        // Логин
        LoginBodyRecordsModel loginData = new LoginBodyRecordsModel(td.username, td.password);
        SuccessfulLoginResponseRecordsModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseRecordsModel.class);

        return loginResponse.access();
    }
}
