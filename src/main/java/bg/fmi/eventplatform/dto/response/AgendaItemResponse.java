package bg.fmi.eventplatform.dto.response;

import bg.fmi.eventplatform.domain.AgendaItem;
import bg.fmi.eventplatform.vo.AgendaItemType;

import java.time.LocalDateTime;

public record AgendaItemResponse(
        Long id,
        Long eventId,
        Long speakerId,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String locationRoom,
        Integer orderIndex,
        AgendaItemType type
) {
    public static AgendaItemResponse fromEntity(AgendaItem item) {
        return new AgendaItemResponse(
                item.getId(),
                item.getEvent().getId(),
                item.getSpeaker() == null ? null : item.getSpeaker().getId(),
                item.getTitle(),
                item.getDescription(),
                item.getStartTime(),
                item.getEndTime(),
                item.getLocationRoom(),
                item.getOrderIndex(),
                item.getType()
        );
    }
}
