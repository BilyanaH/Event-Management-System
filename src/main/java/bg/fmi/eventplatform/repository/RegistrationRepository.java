package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.Registration;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByUserId(Long userId);

    Page<Registration> findByUserId(Long userId, Pageable pageable);

    List<Registration> findByEventId(Long eventId);

    Optional<Registration> findByConfirmationCode(String confirmationCode);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    boolean existsByUserIdAndEventIdAndStatus(Long userId, Long eventId, RegistrationStatus status);

    long countByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);

    @Query("SELECT FUNCTION('DATE', r.registeredAt) AS day, COUNT(r) AS total " +
            "FROM Registration r WHERE r.event.id = :eventId " +
            "GROUP BY FUNCTION('DATE', r.registeredAt) " +
            "ORDER BY FUNCTION('DATE', r.registeredAt)")
    List<Object[]> countRegistrationsPerDay(@Param("eventId") Long eventId);
}
