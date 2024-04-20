package tests;

import io.restassured.RestAssured;
import models.UsersResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.RegistrationSpec.registrationRequestSpec;
import static specs.RegistrationSpec.registrationResponseSpec;

public class GetUsersApiTests extends TestBase {

    @BeforeAll
    public static void beforeAll() {
        TestBase.beforeAll();
        RestAssured.basePath = "api/users";
    }

    @Test
    void checkAllUsersAreShownTest() {
        int totalUsersFromResponce = 0;
        int totalPagesFromResponce = 0;
        int totalUserCounter = 0;
        int pageCounter = 1;
        while (true) {
            int finalPageCounter = pageCounter;
            UsersResponseModel response = step("Make request with page " + pageCounter, () ->
                    given(registrationRequestSpec)
                            .params("page", finalPageCounter)
                            .when()
                            .get()
                            .then()
                            .spec(registrationResponseSpec)
                            .extract().as(UsersResponseModel.class));
            totalUsersFromResponce = response.getTotal();
            totalPagesFromResponce = response.getTotalPages();
            int usersCount = response.getUsers().size();
            if(usersCount == 0)
                break;
            totalUserCounter += usersCount;
            pageCounter++;
        }

        int finalTotalUserCounter = totalUserCounter;
        int finalTotalUsersFromResponce = totalUsersFromResponce;
        int finalTotalPagesFromResponce = totalPagesFromResponce;
        int finalPageCounter = pageCounter;
        step("Check response", () -> {
            assertThat(finalTotalUserCounter).isEqualTo(finalTotalUsersFromResponce);
            assertThat(finalPageCounter - 1).isEqualTo(finalTotalPagesFromResponce);
        });
    }

    @Test
    void checkSuccessGetUsersSchemaTest() {
        step("Check schema of request", () ->
                given(registrationRequestSpec)
                        .params("page", 2)
                        .when()
                        .get()
                        .then()
                        .spec(registrationResponseSpec)
                        .body(matchesJsonSchemaInClasspath(
                                "schemas/success_get_list_users_schema.json")));
    }
    
    @Test
    void checkGetUserRequestWithoutPageParamReturnFirstPageTest() {
        UsersResponseModel response = step("Make request", () ->
                given(registrationRequestSpec)
                        .when()
                        .get()
                        .then()
                        .spec(registrationResponseSpec)
                        .extract().as(UsersResponseModel.class));

        step("Check response", () -> {
            assertThat(response.getPage()).isEqualTo(1);
        });
    }

    @Test
    void checkGetUserRequestWithIrrelevantPageReturnEmptyArrayTest() {
        UsersResponseModel response = step("Make request", () ->
                given(registrationRequestSpec)
                        .params("page", 200)
                        .when()
                        .get()
                        .then()
                        .spec(registrationResponseSpec)
                        .extract().as(UsersResponseModel.class));

        step("Check response", () -> {
            assertThat(response.getUsers().size()).isEqualTo(0);
        });}
}
