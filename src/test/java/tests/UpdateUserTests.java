package tests;

import models.login.LoginBodyRecordsModel;
import models.registration.RegistrationBodyModel;
import models.update_user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
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

        String accessToken = step("Регистрация и авторизация пользователя", () -> registerAndLogin());

        SuccessfulUpdateUserPutResponseModel responseUpdateUser = step("Отправка PUT-запроса на обновление данных", () -> {
            UpdateUserBodyModel dataUpdateUser = new UpdateUserBodyModel(td.username, td.firstName, td.lastName, td.email);
            return given(updateUserRequestSpec)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(dataUpdateUser)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUpdateUserPutResponseModel.class);
        });

        step("Проверка бизнес-логики: валидация обновленных данных пользователя", () -> {
            assertThat(responseUpdateUser.id()).isPositive();
            assertThat(responseUpdateUser.username()).isEqualTo(td.username);
            assertThat(responseUpdateUser.firstName()).isEqualTo(td.firstName);
            assertThat(responseUpdateUser.lastName()).isEqualTo(td.lastName);
            assertThat(responseUpdateUser.email()).isEqualTo(td.email);
            assertThat(responseUpdateUser.remoteAddr()).isNotBlank();
        });
    }



    @DisplayName("Успешное полное обновление пользователя методом patch")
    @Test
    public void successfulPatchUpdateUserTest() {

        String accessToken = step("Регистрация и авторизация пользователя", () -> registerAndLogin());

        SuccessfulUpdateUserPatchResponseModel responseUpdateUser = step("Отправка PATCH-запроса на обновление данных", () -> {
            UpdateUserBodyModel dataUpdateUser = new UpdateUserBodyModel(td.username, td.firstName, td.lastName, td.email);
            return given(updateUserRequestSpec)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(dataUpdateUser)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUpdateUserPatchResponseModel.class);
        });
        step("Проверка бизнес-логики: валидация обновленных данных пользователя", () -> {
            assertThat(responseUpdateUser.id()).isPositive();
            assertThat(responseUpdateUser.username()).isEqualTo(td.username);
            assertThat(responseUpdateUser.firstName()).isEqualTo(td.firstName);
            assertThat(responseUpdateUser.lastName()).isEqualTo(td.lastName);
            assertThat(responseUpdateUser.email()).isEqualTo(td.email);
        });

    }

    @DisplayName("Успешное частичное обновление пользователя")
    @Test
    public void successfulPartialPatchUpdateUserTest() {

        String accessToken = step("Регистрация и авторизация пользователя", () -> registerAndLogin());

        SuccessfulUpdateUserPatchResponseModel responseUpdateUser = step("Отправка PATCH-запроса с частичным обновлением (только username)", () -> {
            PartialUpdateUserBodyModel dataUpdateUser = new PartialUpdateUserBodyModel(td.username);
            return given(updateUserRequestSpec)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(dataUpdateUser)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulUpdateUserResponseSpec)
                    .extract().as(SuccessfulUpdateUserPatchResponseModel.class);
        });

        step("Проверка бизнес-логики: валидация частичного обновления данных", () -> {
            assertThat(responseUpdateUser.id()).isPositive();
            assertThat(responseUpdateUser.username()).isEqualTo(td.username);
        });
    }

    @DisplayName("Частичное обновление пользователя методом put: негативный тест")
    @Test
    public void partialPutUpdateUserTest() {

        String accessToken = step("Регистрация и авторизация пользователя", () -> registerAndLogin());

        InvalidPartialUpdateUserResponseBodyModel responseUpdateUser = step("Отправка PUT-запроса с неполными данными (ожидаем ошибку)", () -> {
            PartialUpdateUserBodyModel dataUpdateUser = new PartialUpdateUserBodyModel(td.username);
            return given(updateUserRequestSpec)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(dataUpdateUser)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(invalidPartialUpdateUserResponseSpec)
                    .extract().as(InvalidPartialUpdateUserResponseBodyModel.class);
        });

        step("Проверка бизнес-логики: валидация текста ошибок валидации полей", () -> {
            assertThat(responseUpdateUser.firstName().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(responseUpdateUser.lastName().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(responseUpdateUser.email().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(responseUpdateUser.username()).isNull();
        });
    }

    // Вспомогательный метод
    private String registerAndLogin() {
        step("Отправка POST-запроса на /users/register/ и проверка HTTP-статуса", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
            given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        return step("Отправка POST-запроса на /auth/token/ и проверка HTTP-статуса", () -> {
            LoginBodyRecordsModel loginData = new LoginBodyRecordsModel(td.username, td.password);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().path("access");
        });
    }

}
