package be.acara.events.service.mapper;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {
    public EventDto map(Event event) {
        EventDto eventDto = EventDto.builder()
                .category(event.getCategory().toString())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .name(event.getName())
                .price(event.getPrice())
                .id(event.getId())
                .build();
        if(event.getImage() != null){
            eventDto.setImage(event.getImage());
        }
        return eventDto;
    }

    public Event map(EventDto eventDto) {
        Event event = Event.builder()
                .category(Category.valueOf(eventDto.getCategory().toUpperCase()))
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .image(eventDto.getImage())
                .location(eventDto.getLocation())
                .name(eventDto.getName())
                .price(eventDto.getPrice())
                .id(eventDto.getId())
                .build();
        if(eventDto.getImage() != null){
            event.setImage(eventDto.getImage());
        }
        return event;
    }

    public List<EventDto> map(List<Event> events) {
        return events.stream().map(this::map).collect(Collectors.toList());
    }
}
