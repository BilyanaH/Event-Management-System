package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.RegistrationRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.RegistrationResponse;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.RegistrationService;
import bg.fmi.eventplatform.service.UserService;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import bg.fmi.eventplatform.vo.TicketStatus;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RegistrationServiceIntegrationTest {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AgendaItemRepository agendaItemRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private EventAnalyticsRepository analyticsRepository;

    private User attendee;
    private User organizer;
    private Event event;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        analyticsRepository.deleteAll();
        feedbackRepository.deleteAll();
        registrationRepository.deleteAll();
        agendaItemRepository.deleteAll();
        ticketRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

        organizer = userService.createUser(new UserRequest(
                "org@gmail.com", "password123", "Org", "User", UserRole.ORGANIZER));
        attendee = userService.createUser(new UserRequest(
                "attendee@gmail.com", "password123", "Att", "Endee", UserRole.ATTENDEE));

        event = new Event();
        event.setOrganizer(organizer);
        event.setTitle("Tech Talk");
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setStatus(EventStatus.PUBLISHED);
        event.setCapacity(50);
        event.setCreatedAt(LocalDateTime.now());
        event = eventRepository.save(event);

        ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setName("Standard");
        ticket.setPrice(BigDecimal.TEN);
        ticket.setQuantityAvailable(5);
        ticket.setQuantitySold(0);
        ticket.setStatus(TicketStatus.AVAILABLE);
        ticket = ticketRepository.save(ticket);
    }

    @Test
    void registerDecrementsAvailableTickets() {
        RegistrationResponse response = registrationService.register(event.getId(),
                new RegistrationRequest(ticket.getId()), attendee);

        assertEquals(RegistrationStatus.CONFIRMED, response.status());
        Ticket reloaded = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertEquals(1, reloaded.getQuantitySold());
    }

    @Test
    void cancelReleasesSeat() throws AccessDeniedException {
        RegistrationResponse created = registrationService.register(event.getId(),
                new RegistrationRequest(ticket.getId()), attendee);

        RegistrationResponse cancelled = registrationService.cancel(created.id(), attendee);

        assertEquals(RegistrationStatus.CANCELLED, cancelled.status());
        Ticket reloaded = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertEquals(0, reloaded.getQuantitySold());
    }

    @Test
    void checkInRequiresOrganizer() {
        RegistrationResponse created = registrationService.register(event.getId(),
                new RegistrationRequest(ticket.getId()), attendee);

        assertThrows(AccessDeniedException.class,
                () -> registrationService.checkIn(created.id(), attendee));
    }

    @Test
    void rejectsDuplicateActiveRegistration() {
        registrationService.register(event.getId(),
                new RegistrationRequest(ticket.getId()), attendee);

        assertThrows(ValidationException.class,
                () -> registrationService.register(event.getId(),
                        new RegistrationRequest(ticket.getId()), attendee));
    }
}
