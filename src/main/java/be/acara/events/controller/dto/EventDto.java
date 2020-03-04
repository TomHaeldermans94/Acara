package be.acara.events.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private Long id;
    private LocalDateTime eventDate;
    private String name;
    private String description;
    private byte[] image;
    private String location;
    private String category;
    private Set<UserDto> attendees;
    private BigDecimal price;
}
