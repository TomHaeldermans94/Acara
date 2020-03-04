package be.acara.events.exceptions;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super("User not found", message);
    }
}
