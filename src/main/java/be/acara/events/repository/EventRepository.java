package be.acara.events.repository;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByOrderByEventDateAsc();
    List<Event> findAllByAttendeesContains(User user);
}
