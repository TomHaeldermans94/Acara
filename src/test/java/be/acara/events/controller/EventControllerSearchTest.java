package be.acara.events.controller;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static be.acara.events.util.EventUtil.RESOURCE_URL;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class EventControllerSearchTest extends EventApiTest {
    
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
    void givenAnonymous_whenSearchForStartDate_thenShouldReturnCorrectEvents() {
        LocalDateTime startDate = LocalDate.of(2020, 12, 1).atStartOfDay();
        EventList answer = given()
                .when()
                .queryParam("startDate", startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .get(RESOURCE_URL + "/search")
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(answer);
        List<EventDto> eventList = answer.getEventList();
        assertThat(eventList).size().isEqualTo(4);
        assertThat(eventList).extracting(EventDto::getEventDate).allMatch(localDateTime -> localDateTime.isAfter(startDate));
    }
    
    @Test
    void givenAnonymous_whenSearchForEndDate_thenShouldReturnCorrectEvents() {
        LocalDateTime endDate = LocalDate.of(2020, 12, 1).atStartOfDay();
        EventList answer = given()
                .when()
                .queryParam("endDate", endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .get(RESOURCE_URL + "/search")
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(answer);
        List<EventDto> eventList = answer.getEventList();
        assertThat(eventList).size().isEqualTo(21);
        assertThat(eventList).extracting(EventDto::getEventDate).allMatch(localDateTime -> localDateTime.isBefore(endDate));
    }
    
    @Test
    void givenAnonymous_whenSearchMinPrice_thenShouldReturnCorrectEvents() {
        EventList minPrice = given()
                .when()
                .queryParam("minPrice", BigDecimal.ZERO)
                .get(RESOURCE_URL + "/search")
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(minPrice);
        List<EventDto> eventList = minPrice.getEventList();
        assertThat(eventList).size().isEqualTo(25);
        assertThat(eventList).extracting(EventDto::getPrice).allMatch(price -> price.compareTo(BigDecimal.ZERO) >= 0);
    }
    
    @Test
    void givenAnonymous_whenSearchMaxPrice_thenShouldReturnCorrectEvents() {
        EventList maxPrice = given()
                .when()
                .queryParam("maxPrice", new BigDecimal("300"))
                .get(RESOURCE_URL + "/search")
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(maxPrice);
        List<EventDto> eventList = maxPrice.getEventList();
        assertThat(eventList).size().isEqualTo(23);
        assertThat(eventList).extracting(EventDto::getPrice).allMatch(price -> price.compareTo(new BigDecimal("300")) <= 0);
    }
    
    @Test
    void givenAnonymous_whenSearchCategory_thenShouldReturnCorrectEvents() {
        EventList category = given()
                .when()
                .queryParam("category", Category.MUSIC.getWebDisplay())
                .get(RESOURCE_URL + "/search")
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(category);
        List<EventDto> eventList = category.getEventList();
        assertThat(eventList).size().isEqualTo(13);
        assertThat(eventList).extracting(EventDto::getCategory).allMatch(s -> s.equalsIgnoreCase(Category.MUSIC.toString()));
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
