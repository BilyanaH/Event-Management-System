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

    @Query("SELECT COALESCE(SUM(t.price * t.quantitySold), 0) FROM Ticket t WHERE t.event.id = :eventId")
    BigDecimal sumRevenueByEventId(@Param("eventId") Long eventId);
}
