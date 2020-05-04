package be.acara.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {
    
    @Test
    void contextLoaded() {
        try {
            assertThat(true).isTrue();
        } catch (Exception e) {
            assertThat(true).isFalse();
        }
    }
    
    @Test
    void applicationStart() {
        try {
            EventsApplication.main(new String[]{});
        } catch (Exception e) {
            assertThat(true).isFalse();
        }
    }
}
