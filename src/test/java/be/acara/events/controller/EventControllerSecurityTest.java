package be.acara.events.controller;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static be.acara.events.testutil.EventUtil.RESOURCE_URL;
import static be.acara.events.testutil.LoginUtil.LOGIN_URL;
import static io.restassured.RestAssured.given;

public class EventControllerSecurityTest extends EventApiTest {
    @Test
    void shouldDisplayEvents_asAnonymous() {
        given()
                .when()
                .get(RESOURCE_URL)
                .then()
                .statusCode(200);
    }
    
    @Test
    void shouldDisplaysEvents_asUser() {
        given()
                .header("Authorization", login("test","test"))
                .when()
                .get(RESOURCE_URL)
                .then()
                .statusCode(200);
    }
    
    @Test
    void shouldDisplayEvents_asAdmin() {
        given()
                .header("Authorization", login("admin","admin"))
                .when()
                .get(RESOURCE_URL)
                .then()
                .statusCode(200);
    }
    
    @Test
    void shouldFailToLogin_unknownUser() {
        String username = "sadasdasd";
        String password = "asdsadasds";
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password))
                .post(LOGIN_URL)
                .then()
                .statusCode(403);
    }
    
    private String login(final String username, final String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password))
                .post(LOGIN_URL)
                .then()
                .statusCode(200)
                .extract()
                .header("Authorization");
    }
}
