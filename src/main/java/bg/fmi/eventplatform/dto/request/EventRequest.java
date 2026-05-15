package bg.fmi.eventplatform.dto.request;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.User;
import bg.fmi.eventplatform.vo.EventCategory;
import bg.fmi.eventplatform.vo.EventStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank String title,
        String description,
        String city,
        String venue,
        String venueAddress,
        @NotNull @Future LocalDateTime startDate,
        @NotNull @Future LocalDateTime endDate,
        @Positive Integer capacity,
        @NotNull EventStatus status,
        EventCategory category,
        String imageUrl
) {
    public static Event toEntity(EventRequest eventRequest, User organizer) {
        Event event = new Event();
        event.setTitle(eventRequest.title());
        event.setDescription(eventRequest.description());
        event.setCity(eventRequest.city());
        event.setVenue(eventRequest.venue());
        event.setVenueAddress(eventRequest.venueAddress());
        event.setStartDate(eventRequest.startDate());
        event.setEndDate(eventRequest.endDate());
        event.setCapacity(eventRequest.capacity());
        event.setStatus(eventRequest.status());
        event.setCategory(eventRequest.category());
        event.setImageUrl(eventRequest.imageUrl());
        event.setCreatedAt(LocalDateTime.now());
        event.setOrganizer(organizer);
        return event;
    }
}