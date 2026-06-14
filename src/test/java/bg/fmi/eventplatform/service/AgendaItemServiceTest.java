package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.AgendaItem;
import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.AgendaItemRequest;
import bg.fmi.eventplatform.dto.response.AgendaItemResponse;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import bg.fmi.eventplatform.vo.AgendaItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaItemServiceTest {

    @Mock
    private AgendaItemRepository agendaItemRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SpeakerRepository speakerRepository;

    @InjectMocks
    private AgendaItemService agendaItemService;

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
    void createAgendaItemSetsOrderIndexFromCount() throws AccessDeniedException {
        AgendaItemRequest request = new AgendaItemRequest("Welcome", null, null,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, null, AgendaItemType.OPENING_SPEECH);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(agendaItemRepository.findByEventIdOrderByOrderIndex(10L)).thenReturn(List.of());
        when(agendaItemRepository.save(any(AgendaItem.class))).thenAnswer(inv -> {
            AgendaItem a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AgendaItemResponse response = agendaItemService.create(10L, request, organizer);

        assertEquals(0, response.orderIndex());
    }

    @Test
    void createThrowsWhenNotOrganizer() {
        User other = new User();
        other.setId(99L);
        AgendaItemRequest request = new AgendaItemRequest("X", null, null,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, null, null);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class, () -> agendaItemService.create(10L, request, other));
    }
}
