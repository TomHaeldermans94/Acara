package be.acara.events.service.mapper;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Event;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    EventDto eventToEventDto(Event event);

    Event eventDtoToEvent(EventDto eventDto);

    default Set<EventDto> eventSetToEventDtoSet(Set<Event> eventSet){
        return eventSet.stream().map(this::eventToEventDto).collect(Collectors.toSet());
    }

    default List<EventDto> eventListToEventDtoList(List<Event> eventSet){
        return eventSet.stream().map(this::eventToEventDto).collect(Collectors.toList());
    }


    default EventList pageToEventList(Page<Event> page) {
        List<EventDto> collect = page
                .getContent()
                .stream()
                .map(this::eventToEventDto)
                .collect(Collectors.toList());
        return new EventList(collect, page.getPageable(), page.getTotalElements());
    }
}
