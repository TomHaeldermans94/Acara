package be.acara.events.service.mapper;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public EventDto map(Event event) {
        return EventDto.builder()
                .category(event.getCategory().toString())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .image(event.getImage())
                .location(event.getLocation())
                .name(event.getName())
                .price(event.getPrice())
                .id(event.getId())
                .build();
    }

    public Event map(EventDto eventDto) {
        return Event.builder()
                .category(Category.valueOf(eventDto.getCategory()))
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .image(eventDto.getImage())
                .location(eventDto.getLocation())
                .name(eventDto.getName())
                .price(eventDto.getPrice())
                .id(eventDto.getId())
                .build();
    }

}
