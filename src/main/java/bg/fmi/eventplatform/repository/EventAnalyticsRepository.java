package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.EventAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventAnalyticsRepository extends JpaRepository<EventAnalytics, Long> {

    Optional<EventAnalytics> findByEventId(Long eventId);
}