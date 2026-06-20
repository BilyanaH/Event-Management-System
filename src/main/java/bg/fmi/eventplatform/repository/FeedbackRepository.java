package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByEventId(Long eventId);

    List<Feedback> findByUserId(Long userId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    long countByEventId(Long eventId);

    @Query(value = "SELECT AVG(overall_rating) FROM feedback WHERE event_id = :eventId", nativeQuery = true)
    Double findAverageOverallRating(@Param("eventId") Long eventId);

    @Query(value = "SELECT AVG(venue_rating) FROM feedback WHERE event_id = :eventId", nativeQuery = true)
    Double findAverageVenueRating(@Param("eventId") Long eventId);

    @Query(value = "SELECT AVG(content_rating) FROM feedback WHERE event_id = :eventId", nativeQuery = true)
    Double findAverageContentRating(@Param("eventId") Long eventId);

    @Query(value = "SELECT AVG(organization_rating) FROM feedback WHERE event_id = :eventId", nativeQuery = true)
    Double findAverageOrganizationRating(@Param("eventId") Long eventId);
}
