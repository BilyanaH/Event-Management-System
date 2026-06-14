package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.TicketRequest;
import bg.fmi.eventplatform.dto.response.TicketResponse;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private TicketService ticketService;

    private User organizer;
    private Event event;
    private TicketRequest request;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);

        event = new Event();
        event.setId(10L);
        event.setOrganizer(organizer);

        request = new TicketRequest("VIP", "VIP access", BigDecimal.valueOf(50), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));
    }

    @Test
    void createTicketSavesAvailable() throws AccessDeniedException {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(99L);
            return t;
        });

        TicketResponse response = ticketService.createTicket(10L, request, organizer);

        assertEquals("VIP", response.name());
        assertEquals(100, response.quantityAvailable());
    }

    @Test
    void createTicketThrowsWhenNotOrganizer() {
        User other = new User();
        other.setId(99L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class, () -> ticketService.createTicket(10L, request, other));
    }
}
