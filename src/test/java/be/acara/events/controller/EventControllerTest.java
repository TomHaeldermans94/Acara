package be.acara.events.controller;

import be.acara.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static be.acara.events.util.EventUtil.RESOURCE_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = EventService.class)
class EventControllerTest {
    @MockBean
    private EventService eventService;
    
    private MockMvc mockMvc;
    /*@InjectMocks
    ah, die stack trace mag je schrappen, zat wat te testen en had de webmvc test niet goed gezet
    private EventController eventController;
    @InjectMocks
    private ControllerExceptionAdvice controllerExceptionAdvice;*/


    @BeforeEach
    void setUp() {
//        standaloneSetup(eventController, controllerExceptionAdvice, springSecurity((request, response, chain) -> chain.doFilter(request, response)));
    }
    
    @Test
    void findById() throws Exception{
        Long id = 1L;
        mockMvc.perform(get(RESOURCE_URL + "/{id}", id))
                .andExpect(status().isOk());
        /*Long id = 1L;
        EventDto eventDto = map(firstEvent());
        when(eventService.findById(id)).thenReturn(eventDto);
        
        EventDto answer = given()
                .when()
                    .get(RESOURCE_URL + "/{id}", id)
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .extract().as(EventDto.class);
        
        assertEvent(answer, eventDto);
        verifyOnce().findById(id);*/
    }
    
    /*@Test
    void findAllByAscendingDate() {
        EventList eventList = createEventListOfSize3();
        when(eventService.findAllByAscendingDate()).thenReturn(eventList);
        
        
        EventList answer = given()
                .when()
                    .get(RESOURCE_URL)
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .extract()
                    .as(EventList.class);
        
        assertEventList(answer, eventList);
        verifyOnce().findAllByAscendingDate();
    }
    
    @Test
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
    void addEvent() {
        EventDto eventDto = map(firstEvent());
        when(eventService.addEvent(eventDto)).thenReturn(eventDto);
    
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
        verifyOnce().addEvent(eventDto);
    }
    
    @Test
    void searchEvent() {
        Map<String, String> searchParams = new HashMap<>();
        when(eventService.search(anyMap())).thenReturn(new EventList());
    
        EventList answer = given()
                    .params(searchParams)
                .when()
                    .get(RESOURCE_URL + "/search")
                .then()
                    .log().ifError()
                    .contentType(ContentType.JSON)
                    .status(HttpStatus.OK)
                    .extract().as(EventList.class);
        
        assertEventList(answer, new EventList());
        verifyOnce().search(Collections.emptyMap());
    }
    
    @Test
    void editEvent() {
        EventDto event = map(firstEvent());
        when(eventService.editEvent(event.getId(), event)).thenReturn(event);
    
        EventDto answer = given()
                    .body(event)
                    .contentType(ContentType.JSON)
                .when()
                    .put(RESOURCE_URL + "/{id}", event.getId())
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .extract().as(EventDto.class);
        
        assertEvent(answer, event);
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
        when(eventService.findAllByAscendingDate()).thenThrow(new RuntimeException());
    
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
        when(eventService.findAllByAscendingDate()).thenThrow(customException);
    
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
        EventList eventList = createEventListOfSize3();

        when(eventService.findEventsByUserId(id)).thenReturn(eventList);

        EventList answer = given()
                .when()
                .get(RESOURCE_URL + "/userevents/{id}",id)
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .extract().as(EventList.class);

        assertEventList(answer, eventList);
        verifyOnce().findEventsByUserId(id);
    }
    
    private void assertEventList(EventList response, EventList expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
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
    }*/
}
