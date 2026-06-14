package bg.fmi.eventplatform.dto.response;

public record FeedbackSummaryResponse(
        Long eventId,
        long totalCount,
        Double averageOverallRating,
        Double averageVenueRating,
        Double averageContentRating,
        Double averageOrganizationRating
) {}
