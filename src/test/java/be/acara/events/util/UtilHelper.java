package be.acara.events.util;


import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import io.restassured.RestAssured;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UtilHelper {
    public static final String RESOURCE_URL = "http://localhost/api/events";
    
    public static void givenAtLeastOneEventExists() {
        if (getNumberOfExistingEvents() == 0) {
            TestRestTemplate testRestTemplate = new TestRestTemplate();
            ResponseEntity<?> responseEntity = testRestTemplate.postForEntity(RESOURCE_URL, anEvent(), null);
            
            if(!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Unable to create event");
            }
        }
    }
    
    public static Event anEvent() {
        return Event.builder()
                .category(Category.MUSIC)
                .description("event description")
                .eventDate(LocalDateTime.now().plusYears(1L))
                .location("location")
                .name("event name")
                .price(BigDecimal.TEN)
                .image(null)
                .build();
    }
    
    public static int getNumberOfExistingEvents() {
        return RestAssured
                .given().auth().preemptive().basic("user", "password")
                .get(RESOURCE_URL).body().jsonPath().getList("eventList").size();
    }
}
