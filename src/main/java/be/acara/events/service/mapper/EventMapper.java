package be.acara.events.service.mapper;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {
    
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);
    
    EventDto eventToEventDto(Event event);
    
    @Mapping(source = "category", target = "category")
    Event eventDtoToEvent(EventDto event);
    
    default EventList pageToEventList(Page<Event> page) {
        List<EventDto> collect = page.getContent().stream().map(this::eventToEventDto).collect(Collectors.toList());
        return new EventList(collect, page.getPageable(), page.getTotalElements());
    }
}
