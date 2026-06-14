package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.EventAnalytics;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.response.EventAnalyticsResponse;
import bg.fmi.eventplatform.repository.EventAnalyticsRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventAnalyticsRepository analyticsRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private User organizer;
    private Event event;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);
        event = new Event();
        event.setId(10L);
        event.setOrganizer(organizer);
    }

    @Test
    void dashboardComputesAndPersists() throws AccessDeniedException {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(registrationRepository.countByEventId(10L)).thenReturn(20L);
        when(registrationRepository.countByEventIdAndStatus(eq(10L), any())).thenReturn(5L);
        when(feedbackRepository.countByEventId(10L)).thenReturn(3L);
        when(feedbackRepository.findAverageOverallRating(10L)).thenReturn(4.5);
        when(ticketRepository.sumRevenueByEventId(10L)).thenReturn(BigDecimal.valueOf(2500));
        when(analyticsRepository.findByEventId(10L)).thenReturn(Optional.empty());
        when(analyticsRepository.save(any(EventAnalytics.class))).thenAnswer(inv -> inv.getArgument(0));

        EventAnalyticsResponse response = analyticsService.dashboard(10L, organizer);

        assertEquals(20, response.totalRegistrations());
        assertEquals(BigDecimal.valueOf(2500), response.revenue());
    }

    @Test
    void dashboardThrowsWhenNotOrganizer() {
        User other = new User();
        other.setId(99L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class, () -> analyticsService.dashboard(10L, other));
    }
}
