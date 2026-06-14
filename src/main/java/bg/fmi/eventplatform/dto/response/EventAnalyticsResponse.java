package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.EventAnalytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventAnalyticsResponse(
        Long eventId,
        Integer totalRegistrations,
        Integer totalCheckIns,
        Integer totalCancellations,
        Integer totalFeedback,
        BigDecimal averageRating,
        BigDecimal revenue,
        LocalDateTime computedAt
) {
    public static EventAnalyticsResponse fromEntity(EventAnalytics analytics) {
        return new EventAnalyticsResponse(
                analytics.getEvent().getId(),
                analytics.getTotalRegistrations(),
                analytics.getTotalCheckIns(),
                analytics.getTotalCancellations(),
                analytics.getTotalFeedback(),
                analytics.getAvgRating(),
                analytics.getRevenue(),
                analytics.getComputedAt()
        );
    }
}
