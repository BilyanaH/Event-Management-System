package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByUserId(Long userId);

    List<Registration> findByEventId(Long eventId);

    Optional<Registration> findByConfirmationCode(String confirmationCode);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}