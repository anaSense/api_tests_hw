package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class GetUsersApiTests {

    @Test
    void checkAllUsersAreShownTest() {
        int totalUsersFromResponce = 0;
        int totalPagesFromResponce = 0;
        int totalUserCounter = 0;
        int pageCounter = 1;
        boolean needContinue = true;
        while (needContinue) {
            Response statusResponse = given()
                    .log().uri()
                    .log().method()
                    .when()
                    .get("https://reqres.in/api/users?page="+pageCounter)
                    .then()
                    .log().status()
                    .log().body()
                    .statusCode(200)
                    .extract().response();
            totalUsersFromResponce = Integer.valueOf(statusResponse.path("total").toString());
            totalPagesFromResponce = Integer.valueOf(statusResponse.path("total_pages").toString());
            String response = statusResponse.asString();
            List<String> users = from(response).getList("data.findAll");
            if(users.size() == 0)
                break;
            totalUserCounter += users.size();
            pageCounter++;
        }
        assertThat(totalUserCounter,equalTo(totalUsersFromResponce));
        assertThat(pageCounter - 1,equalTo(totalPagesFromResponce));
    }

    @Test
    void checkSuccessGetUsersSchemaTest() {
        given()
                .log().uri()
                .log().method()
                .when()
                .get("https://reqres.in/api/users?page=2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/success_get_list_users_schema.json"));
    }


    @Test
    void checkGetUserRequestWithoutPageParamReturnFirstPageTest() {
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .when()
                .get("https://reqres.in/api/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        assertThat(statusResponse.path("page"), is(1));
    }

    @Test
    void checkGetUserRequestWithIrrelevantPageReturnEmptyArrayTest() {
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .when()
                .get("https://reqres.in/api/users?page=200")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        String response = statusResponse.asString();
        List<String> users = from(response).getList("data.findAll");
        assertThat(users.size(), is(0));
    }
}
