package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.TicketRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.TicketResponse;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.TicketService;
import bg.fmi.eventplatform.service.UserService;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TicketServiceIntegrationTest {

    @Autowired
    private TicketService ticketService;
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
    private RegistrationRepository registrationRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private EventAnalyticsRepository analyticsRepository;

    private User organizer;
    private Event event;

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

        event = new Event();
        event.setOrganizer(organizer);
        event.setTitle("Tech Talk");
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreatedAt(LocalDateTime.now());
        event = eventRepository.save(event);
    }

    @Test
    void createAndListTickets() throws AccessDeniedException {
        TicketRequest request = new TicketRequest("Standard", null,
                BigDecimal.valueOf(20), 50, null, LocalDateTime.now().plusDays(5));

        TicketResponse created = ticketService.createTicket(event.getId(), request, organizer);

        List<TicketResponse> all = ticketService.getTicketsForEvent(event.getId());
        assertEquals(1, all.size());
        assertEquals(created.id(), all.get(0).id());
    }

    @Test
    void deleteTicketRemovesIt() throws AccessDeniedException {
        TicketRequest request = new TicketRequest("Standard", null,
                BigDecimal.valueOf(20), 50, null, null);
        TicketResponse created = ticketService.createTicket(event.getId(), request, organizer);

        ticketService.deleteTicket(event.getId(), created.id(), organizer);

        assertTrue(ticketService.getTicketsForEvent(event.getId()).isEmpty());
    }
}
