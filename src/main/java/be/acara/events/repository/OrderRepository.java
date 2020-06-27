package be.acara.events.repository;

import be.acara.events.domain.Event;
import be.acara.events.domain.Order;
import be.acara.events.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository <Order, Long> {
    Order findByEventAndUser(Event event, User user);
}
