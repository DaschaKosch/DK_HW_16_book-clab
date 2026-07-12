package tests;

import models.registration.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class RegistrationTests extends TestBase {
    TestData td = new TestData();


        @DisplayName("Успешная регистрация пользователя")
        @Test
        public void successfulRegistrationTest() {


            SuccessfulRegistrationResponseModel response = step(
                    "Отправка POST-запроса на /users/register/ и проверка HTTP-статуса 201",
                    () -> api.registration.registerUser(td.username, td.password)
            );

            step("Проверка бизнес-логики: валидация данных зарегистрированного пользователя", () -> {
                assertThat(response.username()).isEqualTo(td.username);
                assertThat(response.firstName()).isEqualTo("");
                assertThat(response.lastName()).isEqualTo("");
                assertThat(response.email()).isEqualTo("");
                assertThat(response.id()).isGreaterThan(0);
            });
        }

        @DisplayName("Регистрация уже существующего пользователя: негативный тест")
        @Test
        public void existingUserWrongRegistrationTest() {

            SuccessfulRegistrationResponseModel firstResponse = step(
                    "Отправка POST-запроса на /users/register/ (первая регистрация) и проверка HTTP-статуса 201",
                    () -> api.registration.registerUser(td.username, td.password)
            );

            step("Проверка бизнес-логики: валидация username после первой регистрации", () -> {
                assertThat(firstResponse.username()).isEqualTo(td.username);
            });

            ExistingUserResponseRecordsModel secondResponse = step(
                    "Отправка POST-запроса на /users/register/ (повторная) и проверка HTTP-статуса 400",
                    () -> api.registration.registerExistingUser(td.username, td.password)
            );

            step("Проверка бизнес-логики: валидация ошибки повторной регистрации", () -> {
                assertThat(secondResponse.username().get(0)).isEqualTo(EXPECTED_ERROR_EXISTING_USER);
            });
        }

        @DisplayName("Регистрация с пустым логином: негативный тест")
        @Test
        public void emptyUsernameRegistrationTest() {

        EmptyUsernameResponseModel response = step(
                "Отправка POST-запроса на /users/register/ с пустым логином и проверка HTTP-статуса 400",
                () -> api.registration.registerWithEmptyUsername(td.password)
        );

        step("Проверка бизнес-логики: валидация ошибки пустого логина", () -> {
            assertThat(response.username().get(0)).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
        });
    }

    @DisplayName("Регистрация с пустым паролем: негативный тест")
    @Test
    public void emptyPasswordRegistrationTest() {

        EmptyPasswordResponseModel response = step(
                "Отправка POST-запроса на /users/register/ с пустым паролем и проверка HTTP-статуса 400",
                () -> api.registration.registerWithEmptyPassword(td.username)
        );

        step("Проверка бизнес-логики: валидация ошибки пустого пароля", () -> {
            assertThat(response.password().get(0)).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
        });
    }

    @DisplayName("Регистрация с некорректным логином: негативный тест")
    @Test
    public void wrongUsernameRegistrationTest() {

        WrongUsernameResponseModel response = step(
                "Отправка POST-запроса на /users/register/ с некорректным логином и проверка HTTP-статуса 400",
                () -> api.registration.registerWithWrongUsername(td.wrongUsername, td.password)
        );

        step("Проверка бизнес-логики: валидация ошибки некорректного логина", () -> {
            assertThat(response.username().get(0)).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_CHARACTERS);
        });
    }

    @DisplayName("Регистрация с неверным Content-Type: негативный тест")
    @Test
    public void unsupportedMediaTypeRegistrationTest() {

        UnsupportedMediaTypeRegistrationBodyModel response = step(
                "Отправка POST-запроса на /users/register/ с неверным Content-Type и проверка HTTP-статуса 415",
                () -> api.registration.registerWithUnsupportedMediaType(td.username, td.password)
        );

        step("Проверка бизнес-логики: валидация ошибки неверного Content-Type", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_UNSUPPORTED_MEDIA_TYPE);
        });
    }
    }











