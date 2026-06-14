package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.EventRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.EventResponse;
import bg.fmi.eventplatform.dto.response.EventSummaryResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.EventService;
import bg.fmi.eventplatform.service.UserService;
import bg.fmi.eventplatform.vo.EventCategory;
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
class EventServiceIntegrationTest {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AgendaItemRepository agendaItemRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private EventAnalyticsRepository analyticsRepository;

    private User organizer;

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
    }

    @Test
    void createAndFetchEvent() {
        EventResponse created = eventService.createEvent(buildRequest(), organizer.getId());

        EventResponse fetched = eventService.getEventById(created.id());

        assertEquals("Tech Talk", fetched.title());
    }

    @Test
    void getEventByIdThrowsWhenMissing() {
        assertThrows(EntityNotFoundException.class, () -> eventService.getEventById(999L));
    }

    @Test
    void updateEventThrowsWhenNotOrganizer() {
        EventResponse created = eventService.createEvent(buildRequest(), organizer.getId());
        User other = userService.createUser(new UserRequest(
                "other@gmail.com", "password123", "Other", "User", UserRole.ORGANIZER));

        assertThrows(AccessDeniedException.class,
                () -> eventService.updateEvent(created.id(), buildRequest(), other));
    }

    @Test
    void getEventSummaryReturnsZeroes() {
        EventResponse created = eventService.createEvent(buildRequest(), organizer.getId());

        EventSummaryResponse summary = eventService.getEventSummary(created.id());

        assertEquals(0L, summary.totalRegistrations());
        assertEquals(0L, summary.ticketTypesCount());
        assertEquals(100, summary.availableCapacity());
    }

    private EventRequest buildRequest() {
        return new EventRequest("Tech Talk", "desc", "Sofia", "Hall", "addr",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                100, EventStatus.PUBLISHED, EventCategory.SCIENCE_AND_TECHNOLOGY, null);
    }
}
