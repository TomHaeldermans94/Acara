package be.acara.events.testutil;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "admin",
        username = "admin",
        password = "$2y$10$14ux7BShikxkMJ2.qVyXOOIur8Q0Br58RzfdxYpC9k5DqEXqzEdx.",
        roles = {"USER", "ADMIN"})
public @interface WithMockAdmin {
}
