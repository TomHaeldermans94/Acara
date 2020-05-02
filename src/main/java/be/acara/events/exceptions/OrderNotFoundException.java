package be.acara.events.exceptions;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException(String message) {
        super("Order not found", message);
    }
}
