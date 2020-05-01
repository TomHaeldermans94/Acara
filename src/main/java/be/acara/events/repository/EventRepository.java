package be.acara.events.repository;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Page<Event> findAllByAttendeesContains(User user, Pageable pageable);
    Page<Event> findAllByUsersThatLikeThisEventContains(User user, Pageable pageable);
}
