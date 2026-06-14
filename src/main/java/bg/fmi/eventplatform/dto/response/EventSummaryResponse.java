package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventSummaryResponse(
        Long id,
        String title,
        String city,
        String venue,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer capacity,
        EventStatus status,
        EventCategory category,
        long totalRegistrations,
        long totalCheckIns,
        Integer availableCapacity,
        long ticketTypesCount,
        Double averageRating,
        BigDecimal revenue
) {
    public static EventSummaryResponse of(Event event,
                                          long totalRegistrations,
                                          long totalCheckIns,
                                          Integer availableCapacity,
                                          long ticketTypesCount,
                                          Double averageRating,
                                          BigDecimal revenue) {
        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                event.getCity(),
                event.getVenue(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCapacity(),
                event.getStatus(),
                event.getCategory(),
                totalRegistrations,
                totalCheckIns,
                availableCapacity,
                ticketTypesCount,
                averageRating,
                revenue
        );
    }
}
