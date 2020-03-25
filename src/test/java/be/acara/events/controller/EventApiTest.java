package be.acara.events.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class EventApiTest {

    @LocalServerPort
    private int port;
    
    @Autowired
    protected TestRestTemplate restTemplate;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
    
    protected void initRestTemplate(String user, String password) {
        restTemplate = restTemplate.withBasicAuth(user, password);
    }
}
