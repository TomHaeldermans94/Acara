package be.acara.events.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class EventList extends PageImpl<EventDto> {
    
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EventList(@JsonProperty("content") List<EventDto> content,
                     @JsonProperty("number") int page,
                     @JsonProperty("size") int size,
                     @JsonProperty("totalElements") long total,
                     @JsonProperty("pageable")JsonNode pageable,
                     @JsonProperty("last") boolean last,
                     @JsonProperty("totalPages") int totalPages,
                     @JsonProperty("sort") JsonNode sort,
                     @JsonProperty("first") boolean first,
                     @JsonProperty("numberOfElements") int numberOfElements,
                     @JsonProperty("empty") boolean empty) {
        super(content, PageRequest.of(page, size), total);
    }
    
    public EventList(List<EventDto> content) {
        super(content);
    }
    
    public EventList(List<EventDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
