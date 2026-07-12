package tests;

import models.club.ClubRequestModel;
import models.club.ClubResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

    public class ClubTests extends TestBase {
        TestData td = new TestData();

        @DisplayName("Успешное создание нового книжного клуба авторизованным пользователем")
        @Test
        public void successfulCreateClubTest() {

            String accessToken = step("Регистрация и авторизация пользователя",
                    () -> api.login.registerAndLogin(td.username, td.password)
            );

            ClubRequestModel request = new ClubRequestModel(
                    td.bookTitle,
                    td.bookAuthors,
                    td.publicationYear,
                    td.description,
                    td.telegramChatLink
            );

            ClubResponseModel response = step(
                    "Отправка POST-запроса на /clubs/ и проверка HTTP-статуса 201",
                    () -> api.club.createClub(request, accessToken)
            );

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

            String accessToken = step("Регистрация и авторизация пользователя",
                    () -> api.login.registerAndLogin(td.username, td.password)
            );

            ClubRequestModel createBody = new ClubRequestModel(
                    td.bookTitle,
                    td.bookAuthors,
                    td.publicationYear,
                    td.description,
                    td.telegramChatLink
            );

            ClubResponseModel createdClub = step("Создание клуба для последующего получения",
                    () -> api.club.createClub(createBody, accessToken)
            );

            int clubId = createdClub.id();

            ClubResponseModel retrievedClub = step(
                    "Отправка GET-запроса на /clubs/{id}/ и проверка HTTP-статуса 200",
                    () -> api.club.getClubById(clubId, accessToken)
            );

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

            String accessToken = step("Регистрация и авторизация пользователя",
                    () -> api.login.registerAndLogin(td.username, td.password)
            );

            ClubRequestModel createBody = new ClubRequestModel(
                    td.bookTitle,
                    td.bookAuthors,
                    td.publicationYear,
                    td.description,
                    td.telegramChatLink
            );

            ClubResponseModel createdClub = step("Создание клуба для последующего обновления",
                    () -> api.club.createClub(createBody, accessToken)
            );

            int clubId = createdClub.id();

            ClubRequestModel updateBody = new ClubRequestModel(
                    td.newBookTitle,
                    td.newBookAuthors,
                    td.publicationYear,
                    td.description,
                    td.telegramChatLink
            );

            ClubResponseModel updatedClub = step(
                    "Отправка PUT-запроса на /clubs/{id}/ и проверка HTTP-статуса 200",
                    () -> api.club.updateClub(clubId, updateBody, accessToken)
            );

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

            String accessToken = step("Регистрация и авторизация пользователя",
                    () -> api.login.registerAndLogin(td.username, td.password)
            );

            ClubRequestModel createBody = new ClubRequestModel(
                    td.bookTitle,
                    td.bookAuthors,
                    td.publicationYear,
                    td.description,
                    td.telegramChatLink
            );

            ClubResponseModel createdClub = step("Создание клуба для последующего удаления",
                    () -> api.club.createClub(createBody, accessToken)
            );

            int clubId = createdClub.id();

            step(
                    "Отправка DELETE-запроса на /clubs/{id}/ и проверка HTTP-статуса 204",
                    () -> api.club.deleteClub(clubId, accessToken)
            );

        }
    }

