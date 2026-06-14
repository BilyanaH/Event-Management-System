package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.AgendaItem;
import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Speaker;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.AgendaItemRequest;
import bg.fmi.eventplatform.dto.response.AgendaItemResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.AgendaItemRepository;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgendaItemService {

    private final AgendaItemRepository agendaItemRepository;
    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;

    public AgendaItemService(AgendaItemRepository agendaItemRepository,
                             EventRepository eventRepository,
                             SpeakerRepository speakerRepository) {
        this.agendaItemRepository = agendaItemRepository;
        this.eventRepository = eventRepository;
        this.speakerRepository = speakerRepository;
    }

    public AgendaItemResponse create(Long eventId, AgendaItemRequest request, User principal) throws AccessDeniedException {
        Event event = loadEventForOrganizer(eventId, principal);
        Speaker speaker = resolveSpeaker(request.speakerId());

        AgendaItem item = new AgendaItem();
        item.setEvent(event);
        item.setSpeaker(speaker);
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setStartTime(request.startTime());
        item.setEndTime(request.endTime());
        item.setLocationRoom(request.locationRoom());
        item.setOrderIndex(request.orderIndex() != null
                ? request.orderIndex()
                : (int) agendaItemRepository.findByEventIdOrderByOrderIndex(eventId).size());
        item.setType(request.type());

        return AgendaItemResponse.fromEntity(agendaItemRepository.save(item));
    }

    public List<AgendaItemResponse> listForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found with id " + eventId);
        }
        return agendaItemRepository.findByEventIdOrderByOrderIndex(eventId).stream()
                .map(AgendaItemResponse::fromEntity)
                .toList();
    }

    public AgendaItemResponse update(Long eventId, Long id, AgendaItemRequest request, User principal) throws AccessDeniedException {
        loadEventForOrganizer(eventId, principal);
        AgendaItem item = loadItemForEvent(eventId, id);

        Speaker speaker = resolveSpeaker(request.speakerId());
        item.setSpeaker(speaker);
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setStartTime(request.startTime());
        item.setEndTime(request.endTime());
        item.setLocationRoom(request.locationRoom());
        if (request.orderIndex() != null) {
            item.setOrderIndex(request.orderIndex());
        }
        item.setType(request.type());

        return AgendaItemResponse.fromEntity(agendaItemRepository.save(item));
    }

    public void delete(Long eventId, Long id, User principal) throws AccessDeniedException {
        loadEventForOrganizer(eventId, principal);
        AgendaItem item = loadItemForEvent(eventId, id);
        agendaItemRepository.delete(item);
    }

    @Transactional
    public List<AgendaItemResponse> reorder(Long eventId, List<Long> orderedIds, User principal) throws AccessDeniedException {
        loadEventForOrganizer(eventId, principal);
        List<AgendaItem> items = agendaItemRepository.findByEventIdOrderByOrderIndex(eventId);
        Map<Long, AgendaItem> byId = new HashMap<>();
        for (AgendaItem item : items) {
            byId.put(item.getId(), item);
        }
        if (orderedIds.size() != items.size() || !byId.keySet().containsAll(orderedIds)) {
            throw new ValidationException("orderedIds must reference exactly the agenda items of this event");
        }
        for (int i = 0; i < orderedIds.size(); i++) {
            AgendaItem item = byId.get(orderedIds.get(i));
            item.setOrderIndex(i);
        }
        return agendaItemRepository.saveAll(items).stream()
                .sorted((a, b) -> Integer.compare(a.getOrderIndex(), b.getOrderIndex()))
                .map(AgendaItemResponse::fromEntity)
                .toList();
    }

    private Event loadEventForOrganizer(Long eventId, User principal) throws AccessDeniedException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));
        if (!event.getOrganizer().getId().equals(principal.getId())) {
            throw new AccessDeniedException("User is not the organizer of this event");
        }
        return event;
    }

    private AgendaItem loadItemForEvent(Long eventId, Long id) {
        AgendaItem item = agendaItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agenda item not found with id " + id));
        if (!item.getEvent().getId().equals(eventId)) {
            throw new EntityNotFoundException("Agenda item " + id + " does not belong to event " + eventId);
        }
        return item;
    }

    private Speaker resolveSpeaker(Long speakerId) {
        if (speakerId == null) {
            return null;
        }
        return speakerRepository.findById(speakerId)
                .orElseThrow(() -> new EntityNotFoundException("Speaker not found with id " + speakerId));
    }
}
