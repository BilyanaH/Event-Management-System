package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.AgendaItem;
import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.AgendaItemRequest;
import bg.fmi.eventplatform.dto.response.AgendaItemResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import bg.fmi.eventplatform.vo.AgendaItemType;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;
import net.bytebuddy.asm.Advice;
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
    void testCreateAgendaItemSetsOrderIndexFromCount() throws AccessDeniedException {
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
    void testCreateThrowsWhenNotOrganizer() {
        User other = new User();
        other.setId(99L);
        AgendaItemRequest request = new AgendaItemRequest("X", null, null,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, null, null);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class, () -> agendaItemService.create(10L, request, other));
    }

    @Test
    void testUpdateModifiesFieldsAndReturnsResponse() throws AccessDeniedException {
        event = new Event();
        event.setId(10L);
        event.setOrganizer(organizer);
        event.setTitle("Sofia Tech Conference 2026");
        event.setDescription("Annual technology conference featuring talks on AI, cloud computing, and software engineering.");
        event.setVenue("Sofia Event Center");
        event.setCity("Sofia");
        event.setVenueAddress("bul. Aleksandar Stamboliyski 55");
        event.setStartDate(LocalDateTime.now().plusDays(30));
        event.setEndDate(LocalDateTime.now().plusDays(31));
        event.setCapacity(500);
        event.setStatus(EventStatus.PUBLISHED);
        event.setCategory(EventCategory.SCIENCE_AND_TECHNOLOGY);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        AgendaItem existing = new AgendaItem();
        existing.setId(5L);
        existing.setEvent(event);
        existing.setTitle("old title");
        existing.setOrderIndex(0);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        AgendaItemRequest request = new AgendaItemRequest(
                "title", "something", null,
                start, end, "room", 3, AgendaItemType.PRESENTATION);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(agendaItemRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(agendaItemRepository.save(existing)).thenReturn(existing);

        AgendaItemResponse response = agendaItemService.update(10L, 5L, request, organizer);

        assertEquals("title", response.title());
        assertEquals("something", response.description());
        assertEquals("room", response.locationRoom());
        assertEquals(3, response.orderIndex());
        assertEquals(AgendaItemType.PRESENTATION, response.type());
        verify(agendaItemRepository).save(existing);
    }

    @Test
    void testUpdateThrowsWhenNotOrganizer() {
        User other = new User();
        other.setId(99L);
        AgendaItemRequest request = new AgendaItemRequest("X", null, null,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, null, null);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class,
                () -> agendaItemService.update(10L, 5L, request, other));
    }

    @Test
    void testUpdateThrowsWhenItemNotFound() {
        AgendaItemRequest request = new AgendaItemRequest("X", null, null,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, null, null);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(agendaItemRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> agendaItemService.update(10L, 5L, request, organizer));
    }
}
