package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizerId(Long organizerId);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByCategory(EventCategory category);

    @Query(value = "SELECT * FROM events " +
            "WHERE (:status IS NULL OR status = :status) " +
            "AND (:category IS NULL OR category = :category) " +
            "AND (:from IS NULL OR start_date >= :from) " +
            "AND (:to IS NULL OR end_date <= :to) " +
            "AND (:city IS NULL OR :city = city)",
            countQuery = "SELECT COUNT(*) FROM events " +
                    "WHERE (:status IS NULL OR status = :status) " +
                    "AND (:category IS NULL OR category = :category) " +
                    "AND (:from IS NULL OR start_date >= :from) " +
                    "AND (:to IS NULL OR end_date <= :to) " +
                    "AND (:city IS NULL OR :city = city)",
            nativeQuery = true)
    Page<Event> findEventsWithFilters(@Param("status") String status,
                                      @Param("category") String category,
                                      @Param("city") String city,
                                      @Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to,
                                      Pageable pageable);
}