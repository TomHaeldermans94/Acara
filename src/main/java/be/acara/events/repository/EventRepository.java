package be.acara.events.repository;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Page<Event> findAllByAttendeesContains(User user, Pageable pageable);
    
    Page<Event> findAllByUsersThatLikeThisEventContains(User user, Pageable pageable);
    
    @Query("select e from Event e order by e.attendees.size desc")
    List<Event> findTop4ByAttendeesSize(Pageable pageable);
    
    @Query("select e from Event e where e.eventDate > CURRENT_TIMESTAMP order by e.eventDate asc")
    List<Event> getTop2ByAttendeesContainsAndEventDateAfter(@Param("user") User user, Pageable pageable);
}
