package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {

    List<AgendaItem> findByEventIdOrderByOrderIndex(Long eventId);
}