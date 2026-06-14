package bg.fmi.eventplatform.service;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Feedback;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.dto.request.FeedbackRequest;
import bg.fmi.eventplatform.dto.response.FeedbackResponse;
import bg.fmi.eventplatform.dto.response.FeedbackSummaryResponse;
import bg.fmi.eventplatform.exception.EntityNotFoundException;
import bg.fmi.eventplatform.exception.ValidationException;
import bg.fmi.eventplatform.repository.EventRepository;
import bg.fmi.eventplatform.repository.FeedbackRepository;
import bg.fmi.eventplatform.repository.RegistrationRepository;
import bg.fmi.eventplatform.vo.RegistrationStatus;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           EventRepository eventRepository,
                           RegistrationRepository registrationRepository) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public FeedbackResponse submit(Long eventId, FeedbackRequest request, User principal) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));

        if (feedbackRepository.existsByUserIdAndEventId(principal.getId(), eventId)) {
            throw new ValidationException("Feedback already submitted for this event");
        }
        if (!registrationRepository.existsByUserIdAndEventIdAndStatus(principal.getId(), eventId, RegistrationStatus.CHECKED_IN)) {
            throw new ValidationException("Only checked-in attendees can submit feedback");
        }

        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(principal);
        feedback.setOverallRating(request.overallRating());
        feedback.setComment(request.comment());
        feedback.setVenueRating(request.venueRating());
        feedback.setContentRating(request.contentRating());
        feedback.setOrganizationRating(request.organizationRating());
        feedback.setSubmittedAt(LocalDateTime.now());

        return FeedbackResponse.fromEntity(feedbackRepository.save(feedback));
    }

    public List<FeedbackResponse> listForEvent(Long eventId, User principal) throws AccessDeniedException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));
        if (!event.getOrganizer().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Only the event organizer can list feedback");
        }
        return feedbackRepository.findByEventId(eventId).stream()
                .map(FeedbackResponse::fromEntity)
                .toList();
    }

    public FeedbackSummaryResponse summarize(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found with id " + eventId);
        }
        return new FeedbackSummaryResponse(
                eventId,
                feedbackRepository.countByEventId(eventId),
                feedbackRepository.findAverageOverallRating(eventId),
                feedbackRepository.findAverageVenueRating(eventId),
                feedbackRepository.findAverageContentRating(eventId),
                feedbackRepository.findAverageOrganizationRating(eventId)
        );
    }
}
