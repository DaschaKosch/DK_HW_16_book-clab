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
        SuccessfulRegistrationResponseModel registrationResponse = step("Отправка POST-запроса на /users/register/ и проверка HTTP-статуса 201", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
            return given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseModel.class);
        });

        step("Проверка бизнес-логики: валидация данных зарегистрированного пользователя", () -> {
            assertThat(registrationResponse.username()).isEqualTo(td.username);
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
        SuccessfulRegistrationResponseModel firstRegistrationResponse = step("Отправка POST-запроса на /users/register/ (первая регистрация) и проверка HTTP-статуса 201", () -> {
            return given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseModel.class);
        });

        step("Проверка бизнес-логики: валидация username после первой регистрации", () -> {
            assertThat(firstRegistrationResponse.username()).isEqualTo(td.username);
        });

        ExistingUserResponseRecordsModel secondRegistrationResponse = step("Отправка POST-запроса на /users/register/ (повторная регистрация) и проверка HTTP-статуса 400", () -> {
            return given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongExistingUserRegistrationResponseSpec)
                    .extract()
                    .as(ExistingUserResponseRecordsModel.class);
        });

        step("Проверка бизнес-логики: валидация ошибки повторной регистрации", () -> {
            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(EXPECTED_ERROR_EXISTING_USER);
        });
    }

        @DisplayName("Регистрация пользователя с пустым логином: негативный тест")
        @Test
        public void emptyUsernameRegistrationTest () {
            EmptyUsernameResponseModel emptyUsernameResponseModel = step("Отправка POST-запроса на /users/register/ с пустым логином и проверка HTTP-статуса 400", () -> {
                RegistrationBodyModel registrationData = new RegistrationBodyModel("", td.password);
                return given(registrationRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(emptyUsernameRegistrationResponseSpec)
                        .extract()
                        .as(EmptyUsernameResponseModel.class);
            });

            step("Проверка бизнес-логики: валидация ошибки пустого логина", () -> {
                assertThat(emptyUsernameResponseModel.username().get(0)).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
            });
        }

        @DisplayName("Регистрация пользователя с пустым паролем: негативный тест")
        @Test
        public void emptyPasswordRegistrationTest () {
            EmptyPasswordResponseModel emptyPasswordResponseModel = step("Отправка POST-запроса на /users/register/ с пустым паролем и проверка HTTP-статуса 400", () -> {
                RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, "");
                return given(registrationRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(emptyPasswordRegistrationResponseSpec)
                        .extract()
                        .as(EmptyPasswordResponseModel.class);
            });

            step("Проверка бизнес-логики: валидация ошибки пустого пароля", () -> {
                assertThat(emptyPasswordResponseModel.password().get(0)).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
            });
        }

        @DisplayName("Регистрация пользователя с некорректным логином (с пробелами): негативный тест")
        @Test
        public void wrongUsernameRegistrationTest () {
            WrongUsernameResponseModel wrongUsernameResponseModel = step("Отправка POST-запроса на /users/register/ с некорректным логином и проверка HTTP-статуса 400", () -> {
                RegistrationBodyModel registrationData = new RegistrationBodyModel(td.wrongUsername, td.password);
                return given(registrationRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(wrongUsernameRegistrationResponseSpec)
                        .extract()
                        .as(WrongUsernameResponseModel.class);
            });

            step("Проверка бизнес-логики: валидация ошибки некорректного логина", () -> {
                assertThat(wrongUsernameResponseModel.username().get(0)).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_CHARACTERS);
            });
        }

         @DisplayName("Регистрация с неверным Content-Type: негативный тест")
         @Test
         public void unsupportedMediaTypeRegistrationTest() {
             UnsupportedMediaTypeRegistrationBodyModel unsupportedMediaTypeResponseModel = step("Отправка POST-запроса на /users/register/ с неверным Content-Type и проверка HTTP-статуса 415", () -> {
                 RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
                 return given(unsupportedMediaTypeRegistrationRequestSpec)
                         .body(registrationData)
                         .when()
                         .post("/users/register/")
                         .then()
                         .spec(unsupportedMediaTypeRegistrationResponseSpec)
                         .extract()
                         .as(UnsupportedMediaTypeRegistrationBodyModel.class);
             });

             step("Проверка бизнес-логики: валидация ошибки неверного Content-Type", () -> {
                 assertThat(unsupportedMediaTypeResponseModel.detail()).isEqualTo(EXPECTED_ERROR_UNSUPPORTED_MEDIA_TYPE);
             });
    }

    }











