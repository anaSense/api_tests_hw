package tests;

import io.restassured.RestAssured;
import models.RegistrationBodyModel;
import models.RegistrationErrorResponseModel;
import models.RegistrationResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.RegistrationSpec.*;


public class RegisterUserApiTests extends TestBase {

    @BeforeAll
    public static void beforeAll() {
        TestBase.beforeAll();
        RestAssured.basePath = "api/register";
    }

    @Test
    void successfullyRegistrationTest() {
        RegistrationBodyModel bodyModel = new RegistrationBodyModel();
        bodyModel.setEmail("eve.holt@reqres.in");
        bodyModel.setPassword("pistol");

        RegistrationResponseModel response = step("Make request ", () ->
            given(registrationRequestSpec)
                    .body(bodyModel)
                    .when()
                    .post()
                    .then()
                    .spec(registrationResponseSpec)
                    .extract().as(RegistrationResponseModel.class));

        step("Check response", () -> {
            try {
                assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4");
            } catch (NullPointerException npe) {
                System.out.println("Token is empty");
            }
            assertThat(response.getId()).isEqualTo(4);
        });
    }

    @Test
    void checkSuccessRegistrationSchemaTest() {
        RegistrationBodyModel bodyModel = new RegistrationBodyModel();
        bodyModel.setEmail("eve.holt@reqres.in");
        bodyModel.setPassword("pistol");

        step("Check schema of request", () ->
                given(registrationRequestSpec)
                        .body(bodyModel)
                        .when()
                        .post()
                        .then()
                        .spec(registrationResponseSpec)
                        .body(matchesJsonSchemaInClasspath(
                                "schemas/success_register_schema.json")));
    }

    @Test
    void failedRegistrationWithoutPasswordTest() {
        RegistrationBodyModel bodyModel = new RegistrationBodyModel();
        bodyModel.setEmail("eve.holt@reqres.in");

        RegistrationErrorResponseModel response = step("Make request ", () ->
                given(registrationRequestSpec)
                        .body(bodyModel)
                        .when()
                        .post()
                        .then()
                        .spec(registrationResponseWithErrorSpec)
                        .extract().as(RegistrationErrorResponseModel.class));

        step("Check error in response", () -> {
            assertThat(response.getError()).isEqualTo("Missing password");
        });
    }

    @Test
    void failedRegistrationWithoutEmailTest() {
        RegistrationBodyModel bodyModel = new RegistrationBodyModel();
        bodyModel.setPassword("pistol");

        RegistrationErrorResponseModel response = step("Make request ", () ->
                given(registrationRequestSpec)
                        .body(bodyModel)
                        .when()
                        .post()
                        .then()
                        .spec(registrationResponseWithErrorSpec)
                        .extract().as(RegistrationErrorResponseModel.class));

        step("Check error in response", () -> {
            assertThat(response.getError()).isEqualTo("Missing email or username");
        });
    }

    @Test
    void failedRegistrationWithInvalidEmailTest() {
        RegistrationBodyModel bodyModel = new RegistrationBodyModel();
        bodyModel.setEmail("eve.holtreqres.in");
        bodyModel.setPassword("pistol");

        RegistrationErrorResponseModel response = step("Make request ", () ->
                given(registrationRequestSpec)
                        .body(bodyModel)
                        .when()
                        .post()
                        .then()
                        .spec(registrationResponseWithErrorSpec)
                        .extract().as(RegistrationErrorResponseModel.class));

        step("Check error in response", () -> {
            assertThat(response.getError()).isEqualTo("Note: Only defined users succeed registration");
        });
    }

    @Test
    void failedRegistrationWithEmptyBodyTest() {
        String body = "{}";
        RegistrationErrorResponseModel response = step("Make request ", () ->
                given(registrationRequestSpec)
                        .body(body)
                        .when()
                        .post()
                        .then()
                        .spec(registrationResponseWithErrorSpec)
                        .extract().as(RegistrationErrorResponseModel.class));

        step("Check error in response", () -> {
            assertThat(response.getError()).isEqualTo("Missing email or username");
        });
    }
}
