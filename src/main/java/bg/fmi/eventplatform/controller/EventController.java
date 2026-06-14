package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.EventRequest;
import bg.fmi.eventplatform.dto.response.EventResponse;
import bg.fmi.eventplatform.dto.response.EventSummaryResponse;
import bg.fmi.eventplatform.service.EventService;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/events")
@Tag(name = "Events Api")
public class EventController {
    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    //using @AuthenticationPrincipal, it will work when we add security
    //spring injects the user that sends requests, it looks clearer to me
    @PostMapping
    @Operation(summary = "Create new event")
    public ResponseEntity<EventResponse> createEvent(@RequestBody @Valid EventRequest eventRequest,
                                                     @AuthenticationPrincipal User principal) {
        EventResponse eventResponse = eventService.createEvent(eventRequest, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }

    @GetMapping
    @Operation(summary = "Get events with optional filters")
    public ResponseEntity<Page<EventResponse>> getEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate,asc") String sort) {

        return ResponseEntity.ok(
                eventService.getEvents(status, category, city, dateFrom, dateTo, page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by id")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        EventResponse eventResponse = eventService.getEventById(id);
        return ResponseEntity.ok(eventResponse);
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Event summary with stats")
    public ResponseEntity<EventSummaryResponse> getEventSummary(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventSummary(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing event")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
                                                     @RequestBody @Valid EventRequest eventRequest,
                                                     @AuthenticationPrincipal User principal) throws AccessDeniedException {
        EventResponse eventResponse = eventService.updateEvent(id, eventRequest, principal);
        return ResponseEntity.ok(eventResponse);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change event status")
    public ResponseEntity<EventResponse> changeEventStatus(@PathVariable Long id,
                                                           @RequestParam EventStatus status,
                                                           @AuthenticationPrincipal User principal) throws AccessDeniedException {
        EventResponse eventResponse = eventService.changeEventStatus(id, status, principal);
        return ResponseEntity.ok(eventResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event (organizer only)")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id,
                                            @AuthenticationPrincipal User principal) throws AccessDeniedException {
        eventService.deleteEvent(id, principal);
        return ResponseEntity.noContent().build();
    }
}
