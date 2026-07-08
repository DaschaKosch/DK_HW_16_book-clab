package tests;

import models.registration.*;
import models.examples.records.ExistingUserResponseRecordsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.registration.RegistrationSpec.*;
import static tests.TestData.*;

public class RegistrationTests extends TestBase {
    TestData td = new TestData();

    @DisplayName("Успешная регистрация пользователя")
    @Test
    public void successfulRegistrationTest_with_records() {
        step("Проверить  регистрацию пользователя", () -> {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);

        SuccessfulRegistrationResponseModel registrationResponse = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        String actualUsername = registrationResponse.username();

        assertThat(actualUsername).isEqualTo(td.username);
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");
        assertThat(registrationResponse.id()).isGreaterThan(0);
    });
    }

    @DisplayName("Регистрация уже существующего пользователя: негативный тест")
    @Test
    public void existingUserWrongRegistrationTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
        step("Зарегистрировать пользователя и проверить username в ответе", () -> {
        SuccessfulRegistrationResponseModel firstRegistrationResponse = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        String actualUsername = firstRegistrationResponse.username();
        assertThat(actualUsername).isEqualTo(td.username);
        });

        step("Проверить ошибку  повторной регистрации уже существующего пользователя", () -> {
        ExistingUserResponseRecordsModel secondRegistrationResponse = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongExistingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseRecordsModel.class);

        String actualError = secondRegistrationResponse.username().get(0);
        assertThat(actualError).isEqualTo(EXPECTED_ERROR_EXISTING_USER);
        });
    }

        @DisplayName("Регистрация пользователя с пустым логином: негативный тест")
        @Test
        public void emptyUsernameRegistrationTest () {
            step("Проверить ошибку при регистрации с пустым логином", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel("", td.password);

            EmptyUsernameResponseModel emptyUsernameResponseModel = given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(emptyUsernameRegistrationResponseSpec)
                    .extract()
                    .as(EmptyUsernameResponseModel.class);

            String actualError = emptyUsernameResponseModel.username().get(0);
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
            });
        }

        @DisplayName("Регистрация пользователя с пустым паролем: негативный тест")
        @Test
        public void emptyPasswordRegistrationTest () {
            step("Проверить ошибку при регистрации пользователя с пустым password", () -> {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, "");

            EmptyPasswordResponseModel emptyPasswordResponseModel = given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(emptyPasswordRegistrationResponseSpec)
                    .extract()
                    .as(EmptyPasswordResponseModel.class);

            String actualError = emptyPasswordResponseModel.password().get(0);
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
            });
        }

        @DisplayName("Регистрация пользователя с некорректным логином (с пробелами): негативный тест")
        @Test
        public void wrongUsernameRegistrationTest () {
            step("Проверить ошибку при регистрации пользователя с некорректным логином", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.wrongUsername, td.password);

            WrongUsernameResponseModel wrongUsernameResponseModel = given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongUsernameRegistrationResponseSpec)
                    .extract()
                    .as(WrongUsernameResponseModel.class);

            String actualError = wrongUsernameResponseModel.username().get(0);
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_CHARACTERS);
            });
        }

         @DisplayName("Регистрация с неверным Content-Type: негативный тест")
         @Test
         public void unsupportedMediaTypeRegistrationTest() {
             step("Проверить ошибку при регистрации с неверным Content-Type", () -> {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);

             UnsupportedMediaTypeRegistrationBodyModel unsupportedMediaTypeResponseModel =
                given(unsupportedMediaTypeRegistrationRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(unsupportedMediaTypeRegistrationResponseSpec)
                        .extract()
                        .as(UnsupportedMediaTypeRegistrationBodyModel.class);

        String actualError = unsupportedMediaTypeResponseModel.detail();
        assertThat(actualError).isEqualTo(EXPECTED_ERROR_UNSUPPORTED_MEDIA_TYPE);
             });
    }

    }











