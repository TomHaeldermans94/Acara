package be.acara.events.controller;

import be.acara.events.controller.dto.*;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.exceptions.CustomException;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.service.EventService;
import be.acara.events.service.mapper.CategoryMapper;
import be.acara.events.service.mapper.CategoryMapperImpl;
import be.acara.events.service.mapper.EventMapper;
import be.acara.events.service.mapper.EventMapperImpl;
import be.acara.events.util.EventUtil;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.acara.events.util.EventUtil.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@WebMvcTest(value = EventController.class)
@Import({EventMapperImpl.class, CategoryMapperImpl.class})
class EventControllerTest {
    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private EventMapper eventMapper;
    @MockBean
    private EventService eventService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryMapper categoryMapper;

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
        EventDto eventDto = map(event);
        
        when(eventService.findById(id)).thenReturn(event);
        when(eventMapper.eventToEventDto(event)).thenReturn(eventDto);
        
        EventDto answer = given()
                .when()
                    .get(RESOURCE_URL + "/{id}", id)
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .extract().as(EventDto.class);
        
        assertEvent(answer, eventDto);
        verifyOnce().findById(id);
    }
    
    @Test
    void findAllByAscendingDate() {
        Page<Event> page = createPageOfEventsOfSize3();
        List<EventDto> collect = page.getContent().stream().map(eventMapper::eventToEventDto).collect(Collectors.toList());
        EventList eventDtos = new EventList(collect);
        
        when(eventService.findAllByAscendingDate(any())).thenReturn(page);
        when(eventMapper.pageToEventList(page)).thenReturn(eventDtos);
        
        EventList answer = given()
                .when()
                    .get(RESOURCE_URL)
                .then()
                    .log().ifError()
                    .status(HttpStatus.OK)
                    .contentType(ContentType.JSON)
                    .extract().as(EventList.class);
        
        assertListContent(answer.getContent(), eventDtos.getContent());
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
        List<Category> listOfCategories = List.of(
                Category.MUSIC,
                Category.THEATRE
        );
        List<CategoryDto> categoryDtoList = listOfCategories.stream().map(categoryMapper::categoryToCategoryDto).collect(Collectors.toList());
        CategoriesList categoriesList = new CategoriesList(categoryDtoList);
        
        when(eventService.getAllCategories()).thenReturn(listOfCategories);
        
        CategoriesList answer = given()
                .when()
                    .get(RESOURCE_URL + "/categories")
                .then()
                    .log().ifError()
                    .contentType(ContentType.JSON)
                    .status(HttpStatus.OK)
                    .extract().as(CategoriesList.class);
        
        assertCategoriesList(answer, categoriesList);
        
        verifyOnce().getAllCategories();
    }
    
    @Test
    @WithMockAdmin
    void addEvent() {
        Event event = firstEvent();
        EventDto eventDto = map(event);
        
        when(eventService.addEvent(event)).thenReturn(event);
        when(eventMapper.eventToEventDto(event)).thenReturn(eventDto);
        when(eventMapper.eventDtoToEvent(eventDto)).thenReturn(event);
    
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
        List<EventDto> list = pageOfEventsOfSize3.getContent().stream()
                .map(EventUtil::map)
                .collect(Collectors.toList());
        EventList eventDtos = new EventList(list);
        
        when(eventService.search(anyMap(), any())).thenReturn(pageOfEventsOfSize3);
        when(eventMapper.pageToEventList(pageOfEventsOfSize3)).thenReturn(eventDtos);
    
        EventList answer = given()
                    .params(searchParams)
                .when()
                    .get(RESOURCE_URL + "/search")
                .then()
                    .log().ifError()
                    .contentType(ContentType.JSON)
                    .status(HttpStatus.OK)
                    .extract().as(EventList.class);
        
        assertListContent(answer.getContent(), eventDtos.getContent());
        verifyOnce().search(eq(Collections.emptyMap()), any());
    }
    
    @Test
    @WithMockAdmin
    void editEvent() {
        Event event = firstEvent();
        EventDto eventDto = map(firstEvent());
        
        when(eventService.editEvent(eventDto.getId(), event)).thenReturn(event);
        when(eventMapper.eventDtoToEvent(eventDto)).thenReturn(event);
        when(eventMapper.eventToEventDto(event)).thenReturn(eventDto);
    
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
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .assertThat(mvcResult -> assertThat(mvcResult.getResponse().getStatus()).isEqualTo(500));
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
        EventList eventDtos = EventMapper.INSTANCE.pageToEventList(pageOfEventsOfSize3);
    
        when(eventService.findEventsByUserId(eq(id), any())).thenReturn(pageOfEventsOfSize3);
        when(eventMapper.pageToEventList(pageOfEventsOfSize3)).thenReturn(eventDtos);

        EventList answer = given()
                .when()
                .get(RESOURCE_URL + "/userevents/{id}",id)
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .contentType(ContentType.JSON)
                .extract().as(EventList.class);

        assertListContent(answer.getContent(), eventDtos.getContent());
        verifyOnce().findEventsByUserId(eq(id),any());
    }
    
    private void assertListContent(List<EventDto> response, List<EventDto> expected) {
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
    }
}
