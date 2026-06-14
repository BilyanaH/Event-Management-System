package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.response.AttendancePoint;
import bg.fmi.eventplatform.dto.response.EventAnalyticsResponse;
import bg.fmi.eventplatform.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/analytics")
@Tag(name = "Analytics Api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    @Operation(summary = "Event analytics dashboard")
    public ResponseEntity<EventAnalyticsResponse> dashboard(@PathVariable Long eventId,
                                                            @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(analyticsService.dashboard(eventId, principal));
    }

    @GetMapping("/attendance")
    @Operation(summary = "Attendance over time")
    public ResponseEntity<List<AttendancePoint>> attendance(@PathVariable Long eventId,
                                                            @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(analyticsService.attendance(eventId, principal));
    }
}
