package specs.club;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import specs.user.BaseSpec;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ClubSpec {

    public static RequestSpecification clubRequestSpec = BaseSpec.baseRequestSpec;

    public static ResponseSpecification successfulCreateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/club/create_club_response_schema.json"))
            .build();

    public static ResponseSpecification successfulGetClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/club/get_club_response_schema.json"))
            .build();

    public static ResponseSpecification successfulUpdateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas1/club/update_club_response_schema.json"))
            .build();

    public static ResponseSpecification successfulDeleteClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();
}
