package specs.login;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specs.user.BaseSpec.baseRequestSpec;

public class LoginSpec {

    public static RequestSpecification loginRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/login/successful_login_response_schema.json"))
            .build();

    public static ResponseSpecification wrongCredentialResponseSpecification  = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/login/wrong_credentials_login_response_schema.json"))
            .build();

    public static ResponseSpecification emptyRefreshTokenResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/login/empty_refresh_token_response_schema.json"))
            .build();

    public static ResponseSpecification wrongRefreshTokenResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/login/wrong_refresh_token_response_schema.json"))
            .build();
}
