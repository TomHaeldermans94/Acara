package be.acara.events.exceptions;

@SuppressWarnings("java:S110") // disabled because we know this won't go any deeper
public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException(String message) {
        super("Order not found", message);
    }
}
