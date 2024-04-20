package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static helpers.CustomAllureListener.withCustomTemplates;

import static io.restassured.RestAssured.with;

public class RegistrationSpec {

    public static RequestSpecification registrationRequestSpec = with()
            .filter(withCustomTemplates())
            .log().uri()
            .log().method()
            .log().body()
            .log().headers()
            .contentType(ContentType.JSON);
    public static ResponseSpecification registrationResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(LogDetail.STATUS)
            .log(LogDetail.BODY)
            .build();

    public static ResponseSpecification registrationResponseWithErrorSpec = new ResponseSpecBuilder()
            .expectStatusCode(400)
            .log(LogDetail.STATUS)
            .log(LogDetail.BODY)
            .build();
}
