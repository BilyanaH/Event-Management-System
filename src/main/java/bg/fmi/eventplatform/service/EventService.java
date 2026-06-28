package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.EventRequest;
import bg.fmi.eventplatform.dto.response.EventAnalyticsResponse;
import bg.fmi.eventplatform.dto.response.EventResponse;
import bg.fmi.eventplatform.dto.response.EventSummaryResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.exception.UserNotFoundException;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.TicketRepository;
import bg.fmi.eventplatform.repository.UserRepository;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class EventService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("start_date", "end_date", "created_at", "updated_at");
    private static final String DEFAULT_SORT_FIELD = "start_date";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final AnalyticsService analyticsService;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        TicketRepository ticketRepository,
                        AnalyticsService analyticsService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.analyticsService = analyticsService;
    }

    public EventResponse createEvent(EventRequest eventRequest, Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Event event = EventRequest.toEntity(eventRequest, organizer);
        Event savedEvent = eventRepository.save(event);
        return EventResponse.fromEntity(savedEvent);
    }

    public Page<EventResponse> getEvents(EventStatus eventStatus,
                                         EventCategory eventCategory,
                                         String city,
                                         LocalDateTime from,
                                         LocalDateTime to,
                                         int page,
                                         int size,
                                         String sort) {
        String status = eventStatus == null ? null : eventStatus.toString();
        String category = eventCategory == null ? null : eventCategory.toString();
        String sortField = DEFAULT_SORT_FIELD;
        Sort.Direction sortDirection = DEFAULT_SORT_DIRECTION;
        if (sort != null) {
            String[] sortParts = sort.split(",");
            sortField = ALLOWED_SORT_FIELDS.contains(sortParts[0].strip()) ? sortParts[0].strip() : DEFAULT_SORT_FIELD;
            if (sortParts.length == 2) {
                sortDirection = sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        return eventRepository.findEventsWithFilters(status,
                        category,
                        city,
                        from,
                        to,
                        pageable)
                .map(EventResponse::fromEntity);

    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));
        return EventResponse.fromEntity(event);
    }

    public EventSummaryResponse getEventSummary(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));

        EventAnalyticsResponse analytics = analyticsService.computeAnalytics(id, event);

        int activeRegistrations = analytics.totalRegistrations() - analytics.totalCancellations();
        Integer availableCapacity = event.getCapacity() == null ? null : Math.max(0, event.getCapacity() - activeRegistrations);
        long ticketTypesCount = ticketRepository.countByEventId(id);
        Double avgRating = analytics.averageRating() == null ? null : analytics.averageRating().doubleValue();
        BigDecimal revenue = analytics.revenue() == null ? BigDecimal.ZERO : analytics.revenue();
        return EventSummaryResponse.of(event,
                analytics.totalRegistrations(),
                analytics.totalCheckIns(),
                availableCapacity,
                ticketTypesCount,
                avgRating,
                revenue);
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest eventRequest, User organizer) throws AccessDeniedException {
        Event savedEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));

        if (!savedEvent.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("User is not the organizer of this event");
        }
        savedEvent.setTitle(eventRequest.title());
        savedEvent.setDescription(eventRequest.description());
        savedEvent.setCity(eventRequest.city());
        savedEvent.setVenue(eventRequest.venue());
        savedEvent.setVenueAddress(eventRequest.venueAddress());
        savedEvent.setStartDate(eventRequest.startDate());
        savedEvent.setEndDate(eventRequest.endDate());
        savedEvent.setCapacity(eventRequest.capacity());
        savedEvent.setStatus(eventRequest.status());
        savedEvent.setCategory(eventRequest.category());
        savedEvent.setImageUrl(eventRequest.imageUrl());
        savedEvent.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(savedEvent);

        return EventResponse.fromEntity(savedEvent);
    }

    public EventResponse changeEventStatus(Long id, EventStatus status, User organizer) throws AccessDeniedException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("User is not the organizer of this event");
        }

        event.setStatus(status);
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        return EventResponse.fromEntity(event);
    }

    public void deleteEvent(Long id, User organizer) throws AccessDeniedException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("User is not the organizer of this event");
        }

        eventRepository.delete(event);
    }
}
