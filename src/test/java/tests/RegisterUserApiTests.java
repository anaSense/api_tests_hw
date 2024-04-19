package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class RegisterUserApiTests {

    @Test
    void successfullyRegistrationTest() {
        String body = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }";
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        assertThat(statusResponse.path("token"), is("QpwL5tke4Pnpja7X4"));
        assertThat(statusResponse.path("id"), is(4));
    }

    @Test
    void checkSuccessRegistrationSchemaTest() {
        String body = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }";
        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/success_register_schema.json"));
    }

    @Test
    void failedRegistrationWithoutPasswordTest() {
        String body = "{ \"email\": \"eve.holt@reqres.in\" }";
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .extract().response();

        assertThat(statusResponse.path("error"), is("Missing password"));
    }

    @Test
    void failedRegistrationWithoutEmailTest() {
        String body = "{ \"password\": \"pistol\" }";
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .extract().response();

        assertThat(statusResponse.path("error"), is("Missing email or username"));
    }

    @Test
    void failedRegistrationWithInvalidEmailTest() {
        String body = "{ \"email\": \"eve.holtreqres.in\", \"password\": \"pistol\" }";
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .extract().response();

        assertThat(statusResponse.path("error"), is("Note: Only defined users succeed registration"));
    }

    @Test
    void failedRegistrationWithEmptyBodyTest() {
        String body = "{}";
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .extract().response();

        assertThat(statusResponse.path("error"), is("Missing email or username"));
    }
}
