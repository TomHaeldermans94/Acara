package be.acara.events.controller;

import be.acara.events.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EventControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Test
    void findById() throws Exception {
        Long id = 1L;
        mockMvc.perform(get(String.format("/api/events/%d", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.eventDate").value(LocalDateTime.of(2020,12,20,20,30,54).toString()))
                .andExpect(jsonPath("$.description").value("test description"))
                .andExpect(jsonPath("$.image").value(compareBase64Image()))
                .andExpect(jsonPath("$.location").value("genk"))
                .andExpect(jsonPath("$.category").value(Category.MUSIC.name()))
                .andExpect(jsonPath("$.price").value("20.0"));
    }

    @Test
    void findAll() throws Exception {
        Long idOne = 1L;
        Long idTwo = 2L;
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventList", hasSize(2)))
                .andExpect(jsonPath("$.eventList[0].id").value(idOne))
                .andExpect(jsonPath("$.eventList[0].eventDate").value(LocalDateTime.of(2020,12,20,20,30,54).toString()))
                .andExpect(jsonPath("$.eventList[0].description").value("test description"))
                .andExpect(jsonPath("$.eventList[0].image").value(compareBase64Image()))
                .andExpect(jsonPath("$.eventList[0].location").value("genk"))
                .andExpect(jsonPath("$.eventList[0].category").value(Category.MUSIC.name()))
                .andExpect(jsonPath("$.eventList[0].price").value("20.0"))
                .andExpect(jsonPath("$.eventList[1].id").value(idTwo))
                .andExpect(jsonPath("$.eventList[1].eventDate").value(LocalDateTime.of(2020,11,21,21,31,55).toString()))
                .andExpect(jsonPath("$.eventList[1].description").value("test description2"))
                .andExpect(jsonPath("$.eventList[1].image").value(compareBase64Image()))
                .andExpect(jsonPath("$.eventList[1].location").value("hasselt"))
                .andExpect(jsonPath("$.eventList[1].category").value(Category.MUSIC.name()))
                .andExpect(jsonPath("$.eventList[1].price").value("21.0"));
    }
    
    @Test
    void findById_notFound() throws Exception {
        Long id = Long.MAX_VALUE;
        mockMvc.perform(get(String.format("/api/events/%d", id)))
                .andExpect(status().isNotFound());
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
