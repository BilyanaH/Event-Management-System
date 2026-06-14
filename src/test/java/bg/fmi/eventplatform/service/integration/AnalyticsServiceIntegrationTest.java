package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.EventAnalyticsResponse;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.AnalyticsService;
import bg.fmi.eventplatform.service.UserService;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AnalyticsServiceIntegrationTest {

    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventAnalyticsRepository analyticsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AgendaItemRepository agendaItemRepository;

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
        event.setTitle("Talk");
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreatedAt(LocalDateTime.now());
        event = eventRepository.save(event);
    }

    @Test
    void dashboardOnEmptyEventReturnsZeros() throws AccessDeniedException {
        EventAnalyticsResponse response = analyticsService.dashboard(event.getId(), organizer);

        assertEquals(0, response.totalRegistrations());
        assertEquals(0, response.totalCheckIns());
        assertEquals(0, response.totalFeedback());
    }

    @Test
    void dashboardThrowsWhenNotOrganizer() {
        User other = userService.createUser(new UserRequest(
                "other@gmail.com", "password123", "O", "Other", UserRole.ORGANIZER));

        assertThrows(AccessDeniedException.class,
                () -> analyticsService.dashboard(event.getId(), other));
    }
}
