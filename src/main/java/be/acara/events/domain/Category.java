package be.acara.events.domain;

import lombok.Getter;

@Getter
public enum Category {
    MUSIC("Music"),
    THEATRE("Theatre"),
    UNKNOWN("Unknown");

    private String webDisplay;

    Category(String category) {
        webDisplay = category;
    }
}
