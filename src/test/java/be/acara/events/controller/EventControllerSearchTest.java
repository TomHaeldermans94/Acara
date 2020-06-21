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

import static be.acara.events.testutil.EventUtil.RESOURCE_URL;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class EventControllerSearchTest extends EventApiTest {
    
    @Test
    void givenAnonymous_whenSearchForLocation_thenShouldReturnCorrectEvents() {
        EventList answer = given()
                .when()
                .queryParam("location", "genk")
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(answer);
        List<EventDto> eventList = answer.getContent();
        assertThat(eventList).extracting(EventDto::getLocation).containsOnly("genk");
        assertThat(eventList).size().isEqualTo(1);
    }
    
    @Test
    void givenAnonymous_whenSearchForStartDate_thenShouldReturnCorrectEvents() {
        LocalDateTime startDate = LocalDate.of(2020, 12, 1).atStartOfDay();
        EventList answer = given()
                .when()
                .queryParam("startDate", startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(answer);
        List<EventDto> eventList = answer.getContent();
        assertThat(eventList).size().isEqualTo(4);
        assertThat(eventList).extracting(EventDto::getEventDate).allMatch(localDateTime -> localDateTime.isAfter(startDate));
    }
    
    @Test
    void givenAnonymous_whenSearchForEndDate_thenShouldReturnCorrectEvents() {
        LocalDateTime endDate = LocalDate.of(2020, 12, 1).atStartOfDay();
        EventList answer = given()
                .when()
                .queryParam("endDate", endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(answer);
        List<EventDto> eventList = answer.getContent();
        assertThat(answer.getTotalElements()).isEqualTo(20);
        assertThat(eventList).extracting(EventDto::getEventDate).allMatch(localDateTime -> localDateTime.isBefore(endDate));
    }
    
    @Test
    void givenAnonymous_whenSearchMinPrice_thenShouldReturnCorrectEvents() {
        EventList minPrice = given()
                .when()
                .queryParam("minPrice", BigDecimal.ZERO)
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(minPrice);
        List<EventDto> eventList = minPrice.getContent();
        assertThat(minPrice.getTotalElements()).isEqualTo(24);
        assertThat(eventList).extracting(EventDto::getPrice).allMatch(price -> price.compareTo(BigDecimal.ZERO) >= 0);
    }
    
    @Test
    void givenAnonymous_whenSearchMaxPrice_thenShouldReturnCorrectEvents() {
        EventList maxPrice = given()
                .when()
                .queryParam("maxPrice", new BigDecimal("300"))
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(maxPrice);
        List<EventDto> eventList = maxPrice.getContent();
        assertThat(maxPrice.getTotalElements()).isEqualTo(22);
        assertThat(eventList).extracting(EventDto::getPrice).allMatch(price -> price.compareTo(new BigDecimal("300")) <= 0);
    }
    
    @Test
    void givenAnonymous_whenSearchCategory_thenShouldReturnCorrectEvents() {
        EventList category = given()
                .when()
                .queryParam("category", Category.MUSIC.getWebDisplay())
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(category);
        List<EventDto> eventList = category.getContent();
        assertThat(eventList).size().isEqualTo(23);
        assertThat(eventList).extracting(EventDto::getCategory).allMatch(s -> s.equalsIgnoreCase(Category.MUSIC.toString()));
    }
    
    @Test
    void givenAnonymous_whenSearchName_thenShouldReturnCorrectEvents() {
        EventList names = given()
                .when()
                .queryParam("name", "glue")
                .get(RESOURCE_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);
        
        assertValidEventList(names);
        List<EventDto> eventList = names.getContent();
        assertThat(eventList).size().isEqualTo(1);
        assertThat(eventList).extracting(EventDto::getName).allMatch(s -> s.contains("GlueX"));
    }
    
    private void assertValidEventList(EventList eventList) {
        assertThat(eventList).isNotNull();
        assertThat(eventList.getContent()).isNotNull();
        assertThat(eventList.getContent().size()).isGreaterThan(0);
    }
}
