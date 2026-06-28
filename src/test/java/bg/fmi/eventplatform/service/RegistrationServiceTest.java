package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Registration;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.RegistrationRequest;
import bg.fmi.eventplatform.dto.response.RegistrationResponse;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.vo.EventStatus;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import bg.fmi.eventplatform.vo.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegistrationService registrationService;

    private User user;
    private Event event;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(5L);

        event = new Event();
        event.setId(10L);
        event.setStatus(EventStatus.PUBLISHED);
        event.setCapacity(100);

        ticket = new Ticket();
        ticket.setId(20L);
        ticket.setEvent(event);
        ticket.setQuantityAvailable(10);
        ticket.setQuantitySold(0);
        ticket.setPrice(BigDecimal.TEN);
        ticket.setStatus(TicketStatus.AVAILABLE);
    }

    @Test
    void registerCreatesConfirmedRegistration() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(ticketRepository.findById(20L)).thenReturn(Optional.of(ticket));
        when(registrationRepository.save(any(Registration.class))).thenAnswer(inv -> {
            Registration r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        RegistrationResponse response = registrationService.register(10L, new RegistrationRequest(20L), user);

        assertEquals(RegistrationStatus.CONFIRMED, response.status());
        assertNotNull(response.confirmationCode());
        assertEquals(12, response.confirmationCode().length());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void registerThrowsWhenSoldOut() {
        ticket.setQuantitySold(10);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(ticketRepository.findById(20L)).thenReturn(Optional.of(ticket));

        assertThrows(ValidationException.class,
                () -> registrationService.register(10L, new RegistrationRequest(20L), user));
    }

    @Test
    void registerThrowsForUnpublishedEvent() {
        event.setStatus(EventStatus.DRAFT);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(ValidationException.class,
                () -> registrationService.register(10L, new RegistrationRequest(20L), user));
    }

    @Test
    void registerRejectsDuplicateActive() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(ticketRepository.findById(20L)).thenReturn(Optional.of(ticket));
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(eq(5L), eq(10L), eq(RegistrationStatus.CONFIRMED))).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> registrationService.register(10L, new RegistrationRequest(20L), user));
    }
}
