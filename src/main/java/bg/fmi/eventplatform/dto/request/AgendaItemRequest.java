package bg.fmi.eventplatform.dto.request;

import bg.fmi.eventplatform.vo.AgendaItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendaItemRequest(
        @NotBlank String title,
        String description,
        Long speakerId,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        String locationRoom,
        Integer orderIndex,
        AgendaItemType type
) {}
