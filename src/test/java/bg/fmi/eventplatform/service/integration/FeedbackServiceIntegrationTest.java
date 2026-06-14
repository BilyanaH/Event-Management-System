package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Registration;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.FeedbackRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.FeedbackSummaryResponse;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.FeedbackService;
import bg.fmi.eventplatform.service.UserService;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FeedbackServiceIntegrationTest {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AgendaItemRepository agendaItemRepository;
    @Autowired
    private EventAnalyticsRepository analyticsRepository;

    private User attendee;
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

        User organizer = userService.createUser(new UserRequest(
                "org@gmail.com", "password123", "Org", "User", UserRole.ORGANIZER));
        attendee = userService.createUser(new UserRequest(
                "att@gmail.com", "password123", "Att", "Endee", UserRole.ATTENDEE));

        event = new Event();
        event.setOrganizer(organizer);
        event.setTitle("Talk");
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreatedAt(LocalDateTime.now());
        event = eventRepository.save(event);
    }

    @Test
    void submitRequiresCheckedIn() {
        assertThrows(ValidationException.class,
                () -> feedbackService.submit(event.getId(),
                        new FeedbackRequest(5, "good", 5, 5, 5), attendee));
    }

    @Test
    void summarizeReturnsAverages() {
        // create checked-in registration manually
        bg.fmi.eventplatform.domain.Ticket t = new bg.fmi.eventplatform.domain.Ticket();
        t.setEvent(event);
        t.setName("std");
        t.setPrice(java.math.BigDecimal.TEN);
        t.setQuantityAvailable(5);
        t.setQuantitySold(0);
        t.setStatus(bg.fmi.eventplatform.vo.TicketStatus.AVAILABLE);
        t = ticketRepository.save(t);

        Registration reg = new Registration();
        reg.setUser(attendee);
        reg.setEvent(event);
        reg.setTicket(t);
        reg.setStatus(RegistrationStatus.CHECKED_IN);
        reg.setConfirmationCode("ABC123XYZ012");
        reg.setRegisteredAt(LocalDateTime.now());
        reg.setCheckedInAt(LocalDateTime.now());
        registrationRepository.save(reg);

        feedbackService.submit(event.getId(),
                new FeedbackRequest(4, "good", 5, 5, 4), attendee);

        FeedbackSummaryResponse summary = feedbackService.summarize(event.getId());
        assertEquals(1L, summary.totalCount());
        assertEquals(4.0, summary.averageOverallRating());
    }
}
