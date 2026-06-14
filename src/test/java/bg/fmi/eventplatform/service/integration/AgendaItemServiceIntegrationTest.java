package bg.fmi.eventplatform.service.integration;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.AgendaItemRequest;
import bg.fmi.eventplatform.dto.request.UserRequest;
import bg.fmi.eventplatform.dto.response.AgendaItemResponse;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.service.AgendaItemService;
import bg.fmi.eventplatform.service.UserService;
import bg.fmi.eventplatform.vo.AgendaItemType;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AgendaItemServiceIntegrationTest {

    @Autowired
    private AgendaItemService agendaItemService;
    @Autowired
    private AgendaItemRepository agendaItemRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketRepository ticketRepository;
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
        event.setTitle("Talk");
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreatedAt(LocalDateTime.now());
        event = eventRepository.save(event);
    }

    @Test
    void createAndListAgendaItem() throws AccessDeniedException {
        AgendaItemRequest req = new AgendaItemRequest("Opening", null, null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1),
                "Hall A", null, AgendaItemType.OPENING_SPEECH);

        agendaItemService.create(event.getId(), req, organizer);

        List<AgendaItemResponse> items = agendaItemService.listForEvent(event.getId());
        assertEquals(1, items.size());
        assertEquals("Opening", items.get(0).title());
    }

    @Test
    void reorderUpdatesOrderIndex() throws AccessDeniedException {
        AgendaItemResponse a = agendaItemService.create(event.getId(),
                new AgendaItemRequest("A", null, null, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                        null, null, AgendaItemType.PRESENTATION), organizer);
        AgendaItemResponse b = agendaItemService.create(event.getId(),
                new AgendaItemRequest("B", null, null, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                        null, null, AgendaItemType.PRESENTATION), organizer);

        List<AgendaItemResponse> reordered = agendaItemService.reorder(event.getId(),
                List.of(b.id(), a.id()), organizer);

        assertEquals(b.id(), reordered.get(0).id());
        assertEquals(a.id(), reordered.get(1).id());
    }
}
