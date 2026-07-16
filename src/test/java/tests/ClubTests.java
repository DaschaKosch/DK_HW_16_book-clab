package tests;

import models.club.ClubRequestModel;
import models.club.ClubResponseModel;
import models.login.LoginBodyRecordsModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.club.ClubSpec.clubRequestSpec;

public class ClubTests extends TestBase {
        TestData td = new TestData();

        @DisplayName("Успешное создание нового книжного клуба")
        @Test
        public void successfulCreateClubTest() {

            RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
            LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);

            String accessToken = api.login.registerAndLogin(regBody, loginBody);

            ClubRequestModel request = new ClubRequestModel(
                    td.bookTitle,
                    td.bookAuthors,
                    td.publicationYear,
                    td.description,
                    td.telegramChatLink
            );

            ClubResponseModel response = api.club.createClub(request, accessToken);

            step("Проверка бизнес-логики: валидация данных созданного клуба", () -> {
                assertThat(response.id()).isPositive();
                assertThat(response.bookTitle()).isEqualTo(td.bookTitle);
                assertThat(response.bookAuthors()).isEqualTo(td.bookAuthors);
                assertThat(response.publicationYear()).isEqualTo(td.publicationYear);
                assertThat(response.description()).isEqualTo(td.description);
                assertThat(response.telegramChatLink()).isEqualTo(td.telegramChatLink);
                assertThat(response.owner()).isPositive();
                assertThat(response.members()).isNotEmpty();
                assertThat(response.members()).hasSize(1);
                assertThat(response.reviews()).isEmpty();
                assertThat(response.created()).isNotNull();
            });
        }

        @DisplayName("Успешное получение информации о клубе по ID")
        @Test
        public void getClubByIdTest() {

            RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
            api.registration.registerUser(regBody);

            LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
            String accessToken = api.login.login(loginBody).access();

            ClubRequestModel createBody = new ClubRequestModel(
                    td.bookTitle, td.bookAuthors, td.publicationYear,
                    td.description, td.telegramChatLink
            );

            ClubResponseModel createdClub = api.club.createClub(createBody, accessToken);
            int clubId = createdClub.id();

            ClubResponseModel retrievedClub = api.club.getClubById(clubId, accessToken);

            step("Проверка бизнес-логики: валидация данных полученного клуба", () -> {
                assertThat(retrievedClub.id()).isEqualTo(clubId);
                assertThat(retrievedClub.bookTitle()).isEqualTo(td.bookTitle);
                assertThat(retrievedClub.bookAuthors()).isEqualTo(td.bookAuthors);
                assertThat(retrievedClub.publicationYear()).isEqualTo(td.publicationYear);
                assertThat(retrievedClub.description()).isEqualTo(td.description);
                assertThat(retrievedClub.telegramChatLink()).isEqualTo(td.telegramChatLink);
                assertThat(retrievedClub.owner()).isEqualTo(createdClub.owner());
                assertThat(retrievedClub.members()).hasSize(createdClub.members().size());
                assertThat(retrievedClub.created()).isEqualTo(createdClub.created());
            });
        }

        @DisplayName("Успешное полное обновление клуба")
        @Test
        public void successfulUpdateClubTest() {

            RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
            api.registration.registerUser(regBody);

            LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
            String accessToken = api.login.login(loginBody).access();

            ClubRequestModel createBody = new ClubRequestModel(
                    td.bookTitle, td.bookAuthors, td.publicationYear,
                    td.description, td.telegramChatLink
            );
            ClubResponseModel createdClub = api.club.createClub(createBody, accessToken);
            int clubId = createdClub.id();

            ClubRequestModel updateBody = new ClubRequestModel(
                    td.newBookTitle, td.newBookAuthors, td.publicationYear,
                    td.description, td.telegramChatLink
            );

            ClubResponseModel updatedClub = api.club.updateClub(clubId, updateBody, accessToken);

            step("Проверка бизнес-логики: валидация обновлённых данных клуба", () -> {
                assertThat(updatedClub.id()).isEqualTo(clubId);
                assertThat(updatedClub.bookTitle()).isEqualTo(td.newBookTitle);
                assertThat(updatedClub.bookAuthors()).isEqualTo(td.newBookAuthors);
                assertThat(updatedClub.publicationYear()).isEqualTo(td.publicationYear);
                assertThat(updatedClub.description()).isEqualTo(td.description);
                assertThat(updatedClub.telegramChatLink()).isEqualTo(td.telegramChatLink);
                assertThat(updatedClub.owner()).isEqualTo(createdClub.owner());
                assertThat(updatedClub.members()).containsExactlyInAnyOrderElementsOf(createdClub.members());
                assertThat(updatedClub.created()).isEqualTo(createdClub.created());
            });
        }
        @DisplayName("Успешное удаление клуба")
        @Test
        public void successfulDeleteClubTest() {

            RegistrationBodyModel regBody = new RegistrationBodyModel(td.username, td.password);
            api.registration.registerUser(regBody);

            LoginBodyRecordsModel loginBody = new LoginBodyRecordsModel(td.username, td.password);
            String accessToken = api.login.login(loginBody).access();

            ClubRequestModel createBody = new ClubRequestModel(
                    td.bookTitle, td.bookAuthors, td.publicationYear,
                    td.description, td.telegramChatLink
            );
            ClubResponseModel createdClub = api.club.createClub(createBody, accessToken);
            int clubId = createdClub.id();

            api.club.deleteClub(clubId, accessToken);

            // Проверка удаления — это бизнес-логика, оборачиваем в step()
            step("Проверка бизнес-логики: клуб удалён и недоступен через GET", () -> {
                int statusCode = given(clubRequestSpec)
                        .auth().oauth2(accessToken)
                        .when()
                        .get("/clubs/{id}/", clubId)
                        .then()
                        .extract()
                        .statusCode();
                assertThat(statusCode).isEqualTo(404);
            });
        }
    }

