package be.acara.events.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Lob;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private Long id;
    private int amountOfLikes;
    private boolean liked;
    @FutureOrPresent
    @NotNull
    private LocalDateTime eventDate;
    @Length(min = 2, max = 40)
    private String name;
    @Length(max = 2048)
    private String description;
    @Lob
    private byte[] image;
    @Length(min = 2, max = 40)
    private String location;
    @NotNull
    private String category;

    private BigDecimal price;

    private String youtubeId;

    private Set<EventDto> relatedEvents;
}
