package be.acara.events.controller;

import be.acara.events.domain.Category;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
                .andExpect(jsonPath("$.image").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.location").value("genk"))
                .andExpect(jsonPath("$.category").value(Category.MUSIC.name()))
                .andExpect(jsonPath("$.price").value("20.0"));
    }
    
    @Test
    void findById_notFound() throws Exception {
        Long id = Long.MAX_VALUE;
        mockMvc.perform(get(String.format("/api/events/%d", id)))
                .andExpect(status().isNotFound());
    }
}
