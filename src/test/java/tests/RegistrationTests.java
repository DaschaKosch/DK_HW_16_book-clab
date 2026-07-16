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

            RegistrationBodyModel body = new RegistrationBodyModel(td.username, td.password);
            SuccessfulRegistrationResponseModel response = api.registration.registerUser(body);

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

            RegistrationBodyModel body = new RegistrationBodyModel(td.username, td.password);
            SuccessfulRegistrationResponseModel firstResponse = api.registration.registerUser(body);

            step("Проверка бизнес-логики: валидация username после первой регистрации", () -> {
                assertThat(firstResponse.username()).isEqualTo(td.username);
            });

            ExistingUserResponseRecordsModel secondResponse = api.registration.registerExistingUser(body);

            step("Проверка бизнес-логики: валидация ошибки повторной регистрации", () -> {
                assertThat(secondResponse.username().get(0)).isEqualTo(EXPECTED_ERROR_EXISTING_USER);
            });
        }

        @DisplayName("Регистрация с пустым логином: негативный тест")
        @Test
        public void emptyUsernameRegistrationTest() {

            RegistrationBodyModel body = new RegistrationBodyModel("", td.password);
            EmptyUsernameResponseModel response = api.registration.registerWithEmptyUsername(body);

        step("Проверка бизнес-логики: валидация ошибки пустого логина", () -> {
            assertThat(response.username().get(0)).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
        });
    }

    @DisplayName("Регистрация с пустым паролем: негативный тест")
    @Test
    public void emptyPasswordRegistrationTest() {

        RegistrationBodyModel body = new RegistrationBodyModel(td.username, "");
        EmptyPasswordResponseModel response = api.registration.registerWithEmptyPassword(body);

        step("Проверка бизнес-логики: валидация ошибки пустого пароля", () -> {
            assertThat(response.password().get(0)).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
        });
    }

    @DisplayName("Регистрация с некорректным логином: негативный тест")
    @Test
    public void wrongUsernameRegistrationTest() {

        RegistrationBodyModel body = new RegistrationBodyModel(td.wrongUsername, td.password);
        WrongUsernameResponseModel response = api.registration.registerWithWrongUsername(body);

        step("Проверка бизнес-логики: валидация ошибки некорректного логина", () -> {
            assertThat(response.username().get(0)).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_CHARACTERS);
        });
    }

    @DisplayName("Регистрация с неверным Content-Type: негативный тест")
    @Test
    public void unsupportedMediaTypeRegistrationTest() {

        RegistrationBodyModel body = new RegistrationBodyModel(td.username, td.password);
        UnsupportedMediaTypeRegistrationBodyModel response = api.registration.registerWithUnsupportedMediaType(body);

        step("Проверка бизнес-логики: валидация ошибки неверного Content-Type", () -> {
            assertThat(response.detail()).isEqualTo(EXPECTED_ERROR_UNSUPPORTED_MEDIA_TYPE);
        });
    }
    }











