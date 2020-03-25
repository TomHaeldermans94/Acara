package be.acara.events.controller;

import be.acara.events.controller.dto.ApiError;
import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static be.acara.events.util.UtilHelper.RESOURCE_URL;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class EventControllerGetTest extends EventApiTest {
    
    @Test
    void givenAuthenticatedAsUser_whenGetEvents_thenShouldReturnEvents() {
        EventList answer = given()
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .as(EventList.class);
        
        assertThat(answer).isNotNull();
        
        List<EventDto> eventList = answer.getEventList();
        assertThat(eventList).isNotNull();
        assertThat(eventList.size()).isEqualTo(25);
    }
    
    @Test
    void givenAnonymous_whenGetCategories_thenShouldReturnCategories() {
        CategoriesList answer = given()
                .when()
                .get(RESOURCE_URL + "/categories")
                .then()
                .log().ifError()
                .statusCode(200)
                .extract().as(CategoriesList.class);
        
        assertThat(answer).isNotNull();
    }
    
    @Test
    void givenAnonymous_whenSearchForLocation_thenShouldReturnCorrectEvents() {
        EventList answer = given()
                .when()
                .queryParam("location", "genk")
                .get(RESOURCE_URL + "/search")
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(answer);
        List<EventDto> eventList = answer.getEventList();
        assertThat(eventList).extracting(EventDto::getLocation).containsOnly("genk");
        assertThat(eventList).size().isEqualTo(1);
    }
    
    @Test
    void givenAnonymousAndIdIs1_whenFindById_thenShouldReturnEvent() {
        EventDto answer = given()
                .when()
                .pathParam("id", 1L)
                .get(RESOURCE_URL + "/{id}")
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventDto.class);
        
        assertValidEvent(answer);
    }
    
    @Test
    void givenAnonymousAndIdIsMAX_INT_whenFindById_thenShouldReturn404NotFound() {
        ApiError apierror = given()
                .when()
                .pathParam("id", Integer.MAX_VALUE)
                .get(RESOURCE_URL + "/{id}")
                .then()
                .log().ifError()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .extract().as(ApiError.class);
        
        assertThat(apierror).isNotNull();
        assertThat(apierror.getCode()).isEqualTo(404);
        assertThat(apierror.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(apierror.getTitle()).isEqualTo("Event not found");
        assertThat(apierror.getMessage()).isEqualTo(String.format("Event with ID %d not found", Integer.MAX_VALUE));
    }
    
    private void assertValidEvent(EventDto event) {
        assertThat(event).isNotNull();
        assertThat(event.getName()).isNotNull();
        assertThat(event.getId()).isNotNull();
        assertThat(event.getImage()).isNotNull();
        assertThat(event.getCategory()).isNotNull();
        assertThat(event.getDescription()).isNotNull();
        assertThat(event.getEventDate()).isNotNull();
        assertThat(event.getLocation()).isNotNull();
        assertThat(event.getPrice()).isNotNull();
    }
    
    private void assertValidEventList(EventList eventList) {
        assertThat(eventList).isNotNull();
        assertThat(eventList.getEventList()).isNotNull();
        assertThat(eventList.getEventList().size()).isGreaterThan(0);
    }
}
