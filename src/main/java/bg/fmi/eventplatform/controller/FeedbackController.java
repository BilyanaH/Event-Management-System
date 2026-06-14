package bg.fmi.eventplatform.controller;

import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.FeedbackRequest;
import bg.fmi.eventplatform.dto.response.FeedbackResponse;
import bg.fmi.eventplatform.dto.response.FeedbackSummaryResponse;
import bg.fmi.eventplatform.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/feedback")
@Tag(name = "Feedback Api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @Operation(summary = "Submit feedback")
    public ResponseEntity<FeedbackResponse> submit(@PathVariable Long eventId,
                                                   @RequestBody @Valid FeedbackRequest request,
                                                   @AuthenticationPrincipal User principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.submit(eventId, request, principal));
    }

    @GetMapping
    @Operation(summary = "List feedback (organizer)")
    public ResponseEntity<List<FeedbackResponse>> list(@PathVariable Long eventId,
                                                       @AuthenticationPrincipal User principal) throws AccessDeniedException {
        return ResponseEntity.ok(feedbackService.listForEvent(eventId, principal));
    }

    @GetMapping("/summary")
    @Operation(summary = "Aggregated feedback ratings")
    public ResponseEntity<FeedbackSummaryResponse> summary(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.summarize(eventId));
    }
}
