package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizerId(Long organizerId);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByEventType(EventType eventType);
}