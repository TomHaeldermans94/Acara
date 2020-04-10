package be.acara.events.controller;

import be.acara.events.controller.dto.ApiError;
import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.exceptions.CustomException;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.service.EventService;
import be.acara.events.service.mapper.EventMapper;
import be.acara.events.util.WithMockAdmin;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.acara.events.util.EventUtil.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@WebMvcTest(value = EventController.class)
class EventControllerTest {
    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @SpyBean
    private EventMapper eventMapper;
    @MockBean
    private EventService eventService;
    @Autowired
    private MockMvc mockMvc;

    private final static PageRequest PAGE_REQUEST = PageRequest.of(0,3);

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
    
    @AfterEach
    void tearDown() {
        reset(eventService);
    }
    
    @Test
    void findById(){
        Long id = 1L;
        Event event = firstEvent();
        when(eventService.findById(id)).thenReturn(event);
        
        
        EventDto answer = given()
                .when()
                    .get(RESOURCE_URL + "/{id}", id)
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .extract().as(EventDto.class);
        assertEvent(answer, map(event));
        verifyOnce().findById(id);
    }
    
    @Test
    void findAllByAscendingDate() {
        Page<Event> page = createPageOfEventsOfSize3();
        when(eventService.findAllByAscendingDate(any())).thenReturn(page);
        
        EventList answer = given()
                .when()
                    .get(RESOURCE_URL)
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .contentType(ContentType.JSON)
                    .extract().as(EventList.class);
        
        assertListContent(answer.getContent(), page.getContent());
        verifyOnce().findAllByAscendingDate(any());
    }
    
    @Test
    @WithMockAdmin
    void deleteEvent() {
        Long id = 1L;
        doNothing().when(eventService).deleteEvent(id);
        
        given()
                .when()
                    .delete(RESOURCE_URL + "/{id}", id)
                .then()
                    .log().ifError()
                    .status(HttpStatus.NO_CONTENT);
        
        verifyOnce().deleteEvent(id);
    }
    
    @Test
    void findAllCategories() {
        CategoriesList categoriesList = new CategoriesList(
                List.of(
                        Category.THEATRE.getWebDisplay(),
                        Category.MUSIC.getWebDisplay()
                )
        );
        when(eventService.getAllCategories()).thenReturn(categoriesList);
        
        CategoriesList answer = given()
                .when()
                    .get(RESOURCE_URL + "/categories")
                .then()
                    .log().ifError()
                    .contentType(ContentType.JSON)
                    .status(HttpStatus.OK)
                    .extract().as(CategoriesList.class);
        
        assertCategoriesList(answer,categoriesList);
        
        verifyOnce().getAllCategories();
    }
    
    @Test
    @WithMockAdmin
    void addEvent() {
        Event event = firstEvent();
        EventDto eventDto = map(event);
        when(eventService.addEvent(event)).thenReturn(event);
    
        EventDto answer = given()
                    .body(eventDto)
                    .contentType(ContentType.JSON)
                .when()
                    .post(RESOURCE_URL)
                .then()
                    .log().ifError()
                    .status(HttpStatus.CREATED)
                    .contentType(ContentType.JSON)
                    .extract().as(EventDto.class);
    
        assertEvent(answer, eventDto);
        verifyOnce().addEvent(event);
    }
    
    @Test
    void searchEvent() {
        Map<String, String> searchParams = new HashMap<>();
        Page<Event> pageOfEventsOfSize3 = createPageOfEventsOfSize3();
        when(eventService.search(anyMap(), any())).thenReturn(pageOfEventsOfSize3);
    
        EventList answer = given()
                    .params(searchParams)
                .when()
                    .get(RESOURCE_URL + "/search")
                .then()
                    .log().ifError()
                    .contentType(ContentType.JSON)
                    .status(HttpStatus.OK)
                    .extract().as(EventList.class);
        
        assertListContent(answer.getContent(), pageOfEventsOfSize3.getContent());
        verifyOnce().search(eq(Collections.emptyMap()), any());
    }
    
    @Test
    @WithMockAdmin
    void editEvent() {
        Event event = firstEvent();
        EventDto eventDto = map(firstEvent());
        when(eventService.editEvent(eventDto.getId(), event)).thenReturn(event);
    
        EventDto answer = given()
                    .body(eventDto)
                    .contentType(ContentType.JSON)
                .when()
                    .put(RESOURCE_URL + "/{id}", eventDto.getId())
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .extract().as(EventDto.class);
        
        assertEvent(answer, eventDto);
        verifyOnce().editEvent(firstEvent().getId(), event);
    }
    
    @Test
    void shouldReturnApiError_whenExceptionThrown() {
        Long idToFind = Long.MAX_VALUE;
        EventNotFoundException eventNotFoundException = new EventNotFoundException("event not found");
        when(eventService.findById(idToFind)).thenThrow(eventNotFoundException);
    
        ApiError answer = given()
                .when()
                .get(RESOURCE_URL + "/{id}", idToFind)
                .then()
                .log().all()
                .status(HttpStatus.NOT_FOUND)
                .extract().as(ApiError.class);
        
        assertThat(answer.getStatus()).isEqualTo(eventNotFoundException.getStatus().getReasonPhrase());
        assertThat(answer.getMessage()).isEqualTo(eventNotFoundException.getMessage());
        assertThat(answer.getTitle()).isEqualTo(eventNotFoundException.getTitle());
    }
    
    @Test
    void shouldReturnRegularException_whenNotACustomException() {
        when(eventService.findAllByAscendingDate(any())).thenThrow(new RuntimeException());
        given()
                .when()
                    .get(RESOURCE_URL)
                .then()
                    .log().ifError()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @Test
    void shouldLogError_whenCustom5xxException() {
        CustomException customException = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "test error", "test error");
        when(eventService.findAllByAscendingDate(any())).thenThrow(customException);
    
        ApiError answer = given()
                .when()
                    .get(RESOURCE_URL)
                .then()
                    .log().ifError()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .extract().as(ApiError.class);
        
        assertThat(answer.getTitle()).isEqualTo(customException.getTitle());
        assertThat(answer.getMessage()).isEqualTo(customException.getMessage());
        assertThat(answer.getStatus()).isEqualTo(customException.getStatus().getReasonPhrase());
    }

    @Test
    void findEventsByUserId() {
        Long id = 1L;
        Page<Event> pageOfEventsOfSize3 = createPageOfEventsOfSize3();
        
        when(eventService.findEventsByUserId(eq(id), any())).thenReturn(pageOfEventsOfSize3);

        EventList answer = given()
                .when()
                .get(RESOURCE_URL + "/userevents/{id}",id)
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);

        assertListContent(answer.getContent(), pageOfEventsOfSize3.getContent());
        verifyOnce().findEventsByUserId(eq(id),any());
    }
    
    private void assertListContent(List<EventDto> response, List<Event> expected) {
        assertThat(response).isNotNull();
        List<EventDto> map = eventMapper.map(expected);
        assertThat(response).isEqualTo(map);
    }
    
    private void assertCategoriesList(CategoriesList response, CategoriesList expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }
    
    private void assertEvent(EventDto response, EventDto expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }
    
    private EventService verifyOnce() {
        return verify(eventService, times(1));
    }
}
