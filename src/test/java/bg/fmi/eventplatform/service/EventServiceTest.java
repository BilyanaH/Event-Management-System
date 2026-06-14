package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.EventRequest;
import bg.fmi.eventplatform.dto.response.EventResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private EventService eventService;

    private User organizer;
    private Event event;
    private EventRequest request;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(7L);

        event = new Event();
        event.setId(1L);
        event.setOrganizer(organizer);
        event.setTitle("Tech Talk");
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setStatus(EventStatus.PUBLISHED);
        event.setCapacity(100);

        request = new EventRequest("Tech Talk", "desc", "Sofia", "Hall", "addr",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                100, EventStatus.PUBLISHED, EventCategory.SCIENCE_AND_TECHNOLOGY, null);
    }

    @Test
    void createEventReturnsResponse() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        EventResponse response = eventService.createEvent(request, 7L);

        assertEquals("Tech Talk", response.title());
        assertEquals(7L, response.organizerId());
    }

    @Test
    void getEventByIdThrowsWhenMissing() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventById(1L));
    }

    @Test
    void updateEventThrowsWhenNotOrganizer() {
        User other = new User();
        other.setId(99L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class, () -> eventService.updateEvent(1L, request, other));
    }

    @Test
    void deleteEventDeletesWhenOrganizer() throws AccessDeniedException {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.deleteEvent(1L, organizer);

        verify(eventRepository).delete(event);
    }

    @Test
    void getEventSummaryAggregates() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.countByEventId(1L)).thenReturn(5L);
        when(registrationRepository.countByEventIdAndStatus(eq(1L), any())).thenReturn(2L);
        when(ticketRepository.countByEventId(1L)).thenReturn(3L);
        when(feedbackRepository.findAverageOverallRating(1L)).thenReturn(4.5);
        when(ticketRepository.sumRevenueByEventId(1L)).thenReturn(java.math.BigDecimal.valueOf(500));

        var summary = eventService.getEventSummary(1L);

        assertEquals(5L, summary.totalRegistrations());
        assertEquals(3L, summary.ticketTypesCount());
        assertEquals(4.5, summary.averageRating());
    }

    @Test
    void changeEventStatusThrowsWhenNotOrganizer() {
        User other = new User();
        other.setId(99L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class,
                () -> eventService.changeEventStatus(1L, EventStatus.CANCELLED, other));
    }
}
