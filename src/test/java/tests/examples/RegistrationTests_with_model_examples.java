package tests.examples;

import models.examples.lombok.RegistrationBodyLombokModel;
import models.examples.lombok.RegistrationResponseLombokModel;
import models.examples.pojo.RegistrationBodyPojoModel;
import models.examples.pojo.RegistrationResponsePojoModel;
import models.examples.records.ExistingUserResponseRecordsModel;
import models.examples.records.RegistrationBodyRecordsModel;
import models.examples.records.RegistrationResponseRecordsModel;
import models.login.LoginBodyRecordsModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;

public class RegistrationTests_with_model_examples {
    String username;
    String password;

    @BeforeEach
    public void prepareTestData(){
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }

    @Test
    public void successfulRegistrationTest_with_pojo() {

         RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
         data.setUsername(username);
         data.setPassword(password);

         //RegistrationBodyPojoModel data = new RegistrationBodyPojoModel(username, password);
        // до 4-х значений

        RegistrationResponsePojoModel registrationResponse = given()
                .log().all()
                .contentType(JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponsePojoModel.class);

        assertEquals(username, registrationResponse.getUsername());

    }

    @Test
    public void successfulRegistrationTest_with_lombok() {

        RegistrationBodyLombokModel data = new RegistrationBodyLombokModel();
        data.setUsername(username);
        data.setPassword(password);

        //RegistrationBodyLombokModel data = new RegistrationBodyLombokModel(username, password);
        // до 4-х значений работа с конструктором

        RegistrationResponseLombokModel registrationResponse = given()
                .log().all()
                .contentType(JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponseLombokModel.class);

        assertEquals(username, registrationResponse.getUsername());

    }

    @Test
    public void successfulRegistrationTest_with_records() {

        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username, password);

        RegistrationResponseRecordsModel registrationResponse = given()
                .log().all()
                .contentType(JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponseRecordsModel.class);

        assertEquals(username, registrationResponse.username());

    }

    @Test
    public void existingUser400Test() {

        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username, password);

        given()
                .log().all()
                .contentType(JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());

        ExistingUserResponseRecordsModel response = given()
                .log().all()
                .contentType(JSON)
                .body(data)
                .when()
                .post("https://book-club.qa.guru/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(ExistingUserResponseRecordsModel.class);

        String expectedError = "A user with that username already exists.";

        assertEquals(expectedError, response.username().get(0));


    }

    @Test
    public void negativeRegistrationTest() {

        String data = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(username))
                .body("id", notNullValue());
    }

    @Test
    public void statusSchemaTest() {
        given()
                .log().all()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas1/login/successful_login_response_schema.json"));


    }
    @Test
    public void bestTotalAmountTest() {
        given()
                .log().all()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas1/login/successful_login_response_schema.json"))
                .body("total", is(5));
    }
    @Test
    @DisplayName("Проверка, что value содержит ключи message,ready")
    public void valueObjectContainsMessageAndReady() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/wd/hub/status")
                .then()
                .log().all()
                .body("value", hasKey("message"))
                .body("value", hasKey("ready"));
    }
    @Test
    @DisplayName("Проверка, что значение параметра ready = true")
    public void readyContentTrueTest() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/wd/hub/status")
                .then()
                .log().body()
                .statusCode(200)
                .body("value.ready", is(true));
    }

    @Test
    @DisplayName("Проверка значения ключа message")
    public void messageContentRightTest() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/wd/hub/status")
                .then()
                .log().all()
                .body("value.message", is("Selenoid 1.11.3 built at 2024-05-25_12:34:40PM"));
    }


    /*LoginBodyRecordsModel loginData = new LoginBodyRecordsModel(username,password);

    String refreshToken = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract().path("refresh");



    String logoutData = "{\"refresh\": \"" + refreshToken + "\"}";

    given()
                .log().all()
                .contentType(JSON)
                         .body(logoutData)
                         .basePath("/api/v1")
                         .when()
                         .post("/auth/logout/")
                         .then()
                         .log().all()
                         .statusCode(200);*/
}
