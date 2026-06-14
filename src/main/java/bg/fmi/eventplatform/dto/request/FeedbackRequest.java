package bg.fmi.eventplatform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull @Min(1) @Max(5) Integer overallRating,
        String comment,
        @Min(1) @Max(5) Integer venueRating,
        @Min(1) @Max(5) Integer contentRating,
        @Min(1) @Max(5) Integer organizationRating
) {}
