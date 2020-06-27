package be.acara.events.domain;

import lombok.Getter;

/**
 * A class that holds the predefined Categories
 */
@Getter
public enum Category {
    MUSIC("Music"),
    THEATRE("Theatre"),
    UNKNOWN("Unknown");
    
    /**
     * Holds a normal text version of the Category value
     */
    private String webDisplay;
    
    Category(String category) {
        webDisplay = category;
    }
}
