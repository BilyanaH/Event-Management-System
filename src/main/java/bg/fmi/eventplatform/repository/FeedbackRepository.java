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

    @Query("SELECT AVG(f.overallRating) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageOverallRating(@Param("eventId") Long eventId);

    @Query("SELECT AVG(f.venueRating) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageVenueRating(@Param("eventId") Long eventId);

    @Query("SELECT AVG(f.contentRating) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageContentRating(@Param("eventId") Long eventId);

    @Query("SELECT AVG(f.organizationRating) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageOrganizationRating(@Param("eventId") Long eventId);
}
