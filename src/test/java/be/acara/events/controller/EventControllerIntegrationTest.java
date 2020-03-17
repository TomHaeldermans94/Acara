package be.acara.events.controller;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.domain.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EventControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void findById() throws Exception {
        Long id = 1L;
        mockMvc.perform(get(String.format("/api/events/%d", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.eventDate").value(LocalDateTime.of(2020, 12, 20, 20, 30, 54).toString()))
                .andExpect(jsonPath("$.description").value("test description"))
                .andExpect(jsonPath("$.image").value(compareBase64Image()))
                .andExpect(jsonPath("$.location").value("genk"))
                .andExpect(jsonPath("$.category").value(Category.MUSIC.name()))
                .andExpect(jsonPath("$.price").value("20.0"));
    }
    
    @Test
    void findAll() throws Exception {
        String findAllResponseBody = Files.readString(Paths.get("findAllResponseBody.json"));
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventList", hasSize(25)))
                .andExpect(jsonPath("$.eventList").isArray())
                .andExpect(content().json(findAllResponseBody));
    }
    
    @Test
    void deleteEvent() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/events/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
    }
    
    
    @Test
    void findById_notFound() throws Exception {
        Long id = Long.MAX_VALUE;
        mockMvc.perform(get(String.format("/api/events/%d", id)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteById_notFound() throws Exception {
        Long id = Long.MAX_VALUE;
        mockMvc.perform(delete(String.format("/api/events/%d", id)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void addEvent() throws Exception {
        EventDto eventDto = createEventDto();
        eventDto.setId(null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String json = mapper.writeValueAsString(eventDto);
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andExpect(status().isCreated());
    }
    
    private EventDto createEventDto() {
        return EventDto.builder()
                .id(1L)
                .name("concert")
                .location("genk")
                .category("Music")
                .eventDate(LocalDateTime.of(2020,12,20,20,30,54))
                .description("description")
                .price(new BigDecimal("20.00"))
                .build();
    }
    
    private String compareBase64Image() throws Exception {
        return Base64.getEncoder().encodeToString(getImageBytes("image_event_1.jpg"));
    }
    
    private byte[] getImageBytes(String imageLocation) throws Exception {
        File file = new File(imageLocation);
        FileInputStream fis = new FileInputStream(file);
        return fis.readAllBytes();
    }
}
