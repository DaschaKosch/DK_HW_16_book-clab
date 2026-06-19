package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

public class WdHubStatusTests extends TestBase{

    @Test
    @DisplayName("Корректная авторизация: статус-код 200")
    public void correctCredentialsShouldReturn200() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/status")
                .then()
                .log().all()
                .statusCode(200)
                .body("value.message", is("Selenoid 1.11.3 built at 2024-05-25_12:34:40PM"));
    }

    @Test
    @DisplayName("Некорректный логин: статус-код 401")
    public void incorrectLoginShouldReturn401() {
        given()
                .log().all()
                .auth().basic("user3", "1234")
                .when()
                .get("/status")
                .then()
                .log().all()
                .statusCode(401)
                .body(containsString("Authorization Required"))
                .header("WWW-Authenticate", containsString("Basic"));
    }
    @Test
    @DisplayName("Некорректный пароль: статус-код 401")
    public void incorrectPasswordShouldReturn401() {
        given()
                .log().all()
                .auth().basic("user1", "1233")
                .when()
                .get("/status")
                .then()
                .log().all()
                .statusCode(401)
                .body(containsString("Authorization Required"))
                .header("WWW-Authenticate", containsString("Basic"));
    }

    @Test
    @DisplayName("Без авторизации: статус-код 401")
public void unauthorizedShouldReturn401() {
    given()
            .log().all()
            .when()
            .get("/status")
            .then()
            .log().all()
            .statusCode(401)
            .body(containsString("Authorization Required"))
            .header("WWW-Authenticate", containsString("Basic"));
}
    @Test
    @DisplayName("Ответ соответствует Json-схеме")
    public void responseMatchesJsonSchema() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/status")
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath("sсhemas/status_response_schema.json"));
    }
    @Test
    @DisplayName("Проверка содержимого: value - ключи, типы и значения")
    public void valueContentTest() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/status")
                .then()
                .log().all()
                .statusCode(200)
                .body("value", hasKey("message"))
                .body("value", hasKey("ready"))
                .body("value.ready", is(true))
                .body("value.message", is("Selenoid 1.11.3 built at 2024-05-25_12:34:40PM"));
    }

}
