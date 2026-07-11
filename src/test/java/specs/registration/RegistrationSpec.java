package specs.registration;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specs.user.BaseSpec.baseRequestSpec;

public class RegistrationSpec {

    public static RequestSpecification registrationRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/registration/successful_registration_response_schema.json"))
            .build();

    public static ResponseSpecification wrongExistingUserRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/registration/existing_user_registration_response_schema.json"))
            .build();

    public static ResponseSpecification emptyUsernameRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/registration/emptyUsername_response_registration_schema.json"))
            .build();

    public static ResponseSpecification emptyPasswordRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/registration/emptyPassword_response_registration_schema.json"))
            .build();

    public static ResponseSpecification wrongUsernameRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/registration/emptyUsername_response_registration_schema.json"))
            .build();

    public static RequestSpecification unsupportedMediaTypeRegistrationRequestSpec = with()
            .log().all();



    public static ResponseSpecification unsupportedMediaTypeRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(415)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/registration/unsupported_mediatype_registration_schema.json"))
            .build();

}
