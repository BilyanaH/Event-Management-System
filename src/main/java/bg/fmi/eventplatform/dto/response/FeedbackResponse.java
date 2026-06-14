package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.Feedback;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long id,
        Long userId,
        Long eventId,
        Integer overallRating,
        String comment,
        Integer venueRating,
        Integer contentRating,
        Integer organizationRating,
        LocalDateTime submittedAt
) {
    public static FeedbackResponse fromEntity(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getUser().getId(),
                feedback.getEvent().getId(),
                feedback.getOverallRating(),
                feedback.getComment(),
                feedback.getVenueRating(),
                feedback.getContentRating(),
                feedback.getOrganizationRating(),
                feedback.getSubmittedAt()
        );
    }
}
