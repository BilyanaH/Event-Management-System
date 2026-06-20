package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByEventId(Long eventId);

    long countByEventId(Long eventId);

    @Query(value = "SELECT COALESCE(SUM(price * quantity_sold), 0) FROM tickets WHERE event_id = :eventId", nativeQuery = true)
    BigDecimal sumRevenueByEventId(@Param("eventId") Long eventId);
}
