package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.AgendaItemRequest;
import bg.fmi.eventplatform.dto.response.AgendaItemResponse;
import bg.fmi.eventplatform.service.AgendaItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/agenda")
@Tag(name = "Agenda Api")
public class AgendaItemController {

    private final AgendaItemService agendaItemService;

    public AgendaItemController(AgendaItemService agendaItemService) {
        this.agendaItemService = agendaItemService;
    }

    @PostMapping
    @Operation(summary = "Add agenda item")
    public ResponseEntity<AgendaItemResponse> create(@PathVariable Long eventId,
                                                     @RequestBody @Valid AgendaItemRequest request,
                                                     @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaItemService.create(eventId, request, principal));
    }

    @GetMapping
    @Operation(summary = "Get full agenda")
    public ResponseEntity<List<AgendaItemResponse>> list(@PathVariable Long eventId) {
        return ResponseEntity.ok(agendaItemService.listForEvent(eventId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update agenda item")
    public ResponseEntity<AgendaItemResponse> update(@PathVariable Long eventId,
                                                     @PathVariable Long id,
                                                     @RequestBody @Valid AgendaItemRequest request,
                                                     @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(agendaItemService.update(eventId, id, request, principal));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove agenda item")
    public ResponseEntity<Void> delete(@PathVariable Long eventId,
                                       @PathVariable Long id,
                                       @AuthenticationPrincipal User principal) throws AccessDeniedException {
        agendaItemService.delete(eventId, id, principal);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    @Operation(summary = "Reorder agenda items")
    public ResponseEntity<List<AgendaItemResponse>> reorder(@PathVariable Long eventId,
                                                            @RequestBody List<Long> orderedIds,
                                                            @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(agendaItemService.reorder(eventId, orderedIds, principal));
    }
}
