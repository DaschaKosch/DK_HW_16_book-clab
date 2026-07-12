package tests;

import models.update_user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.EXPECTED_REQUIRED_FIELD;

public class UpdateUserTests extends TestBase {

    TestData td = new TestData();


    @DisplayName("Успешное полное обновление пользователя методом put")
    @Test
    public void successfulPutUpdateUserTest() {


        String accessToken = step("Регистрация и авторизация пользователя",
                () -> api.login.registerAndLogin(td.username, td.password)
        );

        SuccessfulUpdateUserPutResponseModel response = step(
                "Отправка PUT-запроса на /users/me/ и проверка HTTP-статуса 200",
                () -> api.updateUser.updateUserWithPut(
                        accessToken, td.username, td.firstName, td.lastName, td.email)
        );

        step("Проверка бизнес-логики: валидация обновленных данных пользователя", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.username()).isEqualTo(td.username);
            assertThat(response.firstName()).isEqualTo(td.firstName);
            assertThat(response.lastName()).isEqualTo(td.lastName);
            assertThat(response.email()).isEqualTo(td.email);
            assertThat(response.remoteAddr()).isNotBlank();
        });
    }

    @DisplayName("Успешное полное обновление пользователя методом patch")
    @Test
    public void successfulPatchUpdateUserTest() {

        String accessToken = step("Регистрация и авторизация пользователя",
                () -> api.login.registerAndLogin(td.username, td.password)
        );

        SuccessfulUpdateUserPatchResponseModel response = step(
                "Отправка PATCH-запроса на /users/me/ и проверка HTTP-статуса 200",
                () -> api.updateUser.updateUserWithPatch(
                        accessToken, td.username, td.firstName, td.lastName, td.email)
        );

        step("Проверка бизнес-логики: валидация обновленных данных пользователя", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.username()).isEqualTo(td.username);
            assertThat(response.firstName()).isEqualTo(td.firstName);
            assertThat(response.lastName()).isEqualTo(td.lastName);
            assertThat(response.email()).isEqualTo(td.email);
        });
    }

    @DisplayName("Успешное частичное обновление пользователя")
    @Test
    public void successfulPartialPatchUpdateUserTest() {

        String accessToken = step("Регистрация и авторизация пользователя",
                () -> api.login.registerAndLogin(td.username, td.password)
        );

        SuccessfulUpdateUserPatchResponseModel response = step(
                "Отправка PATCH-запроса с частичным обновлением и проверка HTTP-статуса 200",
                () -> api.updateUser.partialUpdateUser(accessToken, td.username)
        );

        step("Проверка бизнес-логики: валидация частичного обновления данных", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.username()).isEqualTo(td.username);
        });
    }

    @DisplayName("Частичное обновление методом put: негативный тест")
    @Test
    public void partialPutUpdateUserTest() {

        String accessToken = step("Регистрация и авторизация пользователя",
                () -> api.login.registerAndLogin(td.username, td.password)
        );

        InvalidPartialUpdateUserResponseBodyModel response = step(
                "Отправка PUT-запроса с неполными данными и проверка HTTP-статуса 400",
                () -> api.updateUser.partialUpdateWithPut(accessToken, td.username)
        );

        step("Проверка бизнес-логики: валидация текста ошибок валидации полей", () -> {
            assertThat(response.firstName().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(response.lastName().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(response.email().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(response.username()).isNull();
        });
    }
    }

