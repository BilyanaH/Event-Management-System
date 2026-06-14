package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.TicketRequest;
import bg.fmi.eventplatform.dto.response.TicketResponse;
import bg.fmi.eventplatform.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/tickets")
@Tag(name = "Tickets Api")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @Operation(summary = "Create ticket type for event")
    public ResponseEntity<TicketResponse> createTicket(@PathVariable Long eventId,
                                                       @RequestBody @Valid TicketRequest request,
                                                       @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.createTicket(eventId, request, principal));
    }

    @GetMapping
    @Operation(summary = "List ticket types for event")
    public ResponseEntity<List<TicketResponse>> listTickets(@PathVariable Long eventId) {
        return ResponseEntity.ok(ticketService.getTicketsForEvent(eventId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket details")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long eventId, @PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicket(eventId, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ticket type")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long eventId,
                                                       @PathVariable Long id,
                                                       @RequestBody @Valid TicketRequest request,
                                                       @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(ticketService.updateTicket(eventId, id, request, principal));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove ticket type")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long eventId,
                                             @PathVariable Long id,
                                             @AuthenticationPrincipal User principal) throws AccessDeniedException {
        ticketService.deleteTicket(eventId, id, principal);
        return ResponseEntity.noContent().build();
    }
}
