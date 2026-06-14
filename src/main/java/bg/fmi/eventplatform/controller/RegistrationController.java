package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.RegistrationRequest;
import bg.fmi.eventplatform.dto.response.RegistrationResponse;
import bg.fmi.eventplatform.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@Tag(name = "Registrations Api")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/events/{eventId}/registrations")
    @Operation(summary = "Register / purchase ticket")
    public ResponseEntity<RegistrationResponse> register(@PathVariable Long eventId,
                                                         @RequestBody @Valid RegistrationRequest request,
                                                         @AuthenticationPrincipal User principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.register(eventId, request, principal));
    }

    @GetMapping("/events/{eventId}/registrations")
    @Operation(summary = "List registrations for event (organizer)")
    public ResponseEntity<List<RegistrationResponse>> listForEvent(@PathVariable Long eventId,
                                                                   @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(registrationService.listForEvent(eventId, principal));
    }

    @GetMapping("/registrations/{id}")
    @Operation(summary = "Get registration details")
    public ResponseEntity<RegistrationResponse> get(@PathVariable Long id,
                                                    @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(registrationService.getById(id, principal));
    }

    @PatchMapping("/registrations/{id}/cancel")
    @Operation(summary = "Cancel registration")
    public ResponseEntity<RegistrationResponse> cancel(@PathVariable Long id,
                                                       @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(registrationService.cancel(id, principal));
    }

    @PatchMapping("/registrations/{id}/check-in")
    @Operation(summary = "Check in attendee")
    public ResponseEntity<RegistrationResponse> checkIn(@PathVariable Long id,
                                                        @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(registrationService.checkIn(id, principal));
    }

    @GetMapping("/users/me/registrations")
    @Operation(summary = "My registrations")
    public ResponseEntity<Page<RegistrationResponse>> mine(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size,
                                                           @AuthenticationPrincipal User principal) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "registeredAt"));
        return ResponseEntity.ok(registrationService.listMine(principal, pageable));
    }
}
