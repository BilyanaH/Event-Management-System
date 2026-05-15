package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        String city,
        String venue,
        String venueAddress,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer capacity,
        EventStatus status,
        EventCategory category,
        String imageUrl,
        Long organizerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EventResponse fromEntity(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCity(),
                event.getVenue(),
                event.getVenueAddress(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCapacity(),
                event.getStatus(),
                event.getCategory(),
                event.getImageUrl(),
                event.getOrganizer().getId(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}