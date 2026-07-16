package tests;

import models.login.LoginBodyRecordsModel;
import models.registration.RegistrationBodyModel;
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

        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);

        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        String accessToken = api.login.login(loginBody).access();

        UpdateUserBodyModel body = new UpdateUserBodyModel(
                td.username, td.firstName, td.lastName, td.email
        );

        SuccessfulUpdateUserPutResponseModel response = api.updateUser.updateUserWithPut(accessToken, body);

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

        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);

        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        String accessToken = api.login.login(loginBody).access();

        UpdateUserBodyModel body = new UpdateUserBodyModel(
                td.username, td.firstName, td.lastName, td.email
        );
        SuccessfulUpdateUserPatchResponseModel response = api.updateUser.updateUserWithPatch(accessToken, body);


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

        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);
        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        String accessToken = api.login.login(loginBody).access();
        PartialUpdateUserBodyModel body = new PartialUpdateUserBodyModel(td.username);
        SuccessfulUpdateUserPatchResponseModel response = api.updateUser.partialUpdateUser(accessToken, body);


        step("Проверка бизнес-логики: валидация частичного обновления данных", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.username()).isEqualTo(td.username);
        });
    }

    @DisplayName("Частичное обновление методом put: негативный тест")
    @Test
    public void partialPutUpdateUserTest() {

        RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
        api.registration.registerUser(regBody);

        LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
        String accessToken = api.login.login(loginBody).access();

        PartialUpdateUserBodyModel body = new PartialUpdateUserBodyModel(td.username);

        InvalidPartialUpdateUserResponseBodyModel response = api.updateUser.partialUpdateWithPut(accessToken, body);


        step("Проверка бизнес-логики: валидация текста ошибок валидации полей", () -> {
            assertThat(response.firstName().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(response.lastName().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(response.email().get(0)).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(response.username()).isNull();
        });
    }
    }

