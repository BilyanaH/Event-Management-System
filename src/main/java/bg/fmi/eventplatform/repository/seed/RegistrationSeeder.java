package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Registration;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import bg.fmi.eventplatform.vo.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Order(6)
@Component
@Profile("!test")
public class RegistrationSeeder implements CommandLineRunner {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationSeeder(RegistrationRepository registrationRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository,
                              TicketRepository ticketRepository,
                              PasswordEncoder passwordEncoder) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (registrationRepository.count() > 0) {
            return;
        }
        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) return;

        User attendee = userRepository.findByEmail("attendee@example.com").orElseGet(() -> {
            UserRequest req = new UserRequest("attendee@example.com", "password123",
                    "Sample", "Attendee", UserRole.ATTENDEE);
            User u = new User(req);
            u.setPassword(passwordEncoder.encode(req.password()));
            return userRepository.save(u);
        });

        Event event = events.get(0);
        Ticket ticket = ticketRepository.findByEventId(event.getId()).stream().findFirst().orElse(null);
        if (ticket == null) return;

        Registration confirmed = new Registration();
        confirmed.setUser(attendee);
        confirmed.setEvent(event);
        confirmed.setTicket(ticket);
        confirmed.setStatus(RegistrationStatus.CONFIRMED);
        confirmed.setConfirmationCode("SEED12345678");
        confirmed.setRegisteredAt(LocalDateTime.now());

        ticket.setQuantitySold(ticket.getQuantitySold() + 1);
        ticketRepository.save(ticket);

        registrationRepository.save(confirmed);
    }
}
