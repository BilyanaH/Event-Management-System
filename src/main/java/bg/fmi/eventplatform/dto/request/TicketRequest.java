package bg.fmi.eventplatform.dto.request;

import bg.fmi.eventplatform.domain.Event;
import bg.fmi.eventplatform.domain.Ticket;
import bg.fmi.eventplatform.vo.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketRequest(
        @NotBlank String name,
        String description,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull @Positive Integer quantityAvailable,
        LocalDateTime saleStart,
        LocalDateTime saleEnd
) {
    public static Ticket toEntity(TicketRequest request, Event event) {
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setName(request.name());
        ticket.setDescription(request.description());
        ticket.setPrice(request.price());
        ticket.setQuantityAvailable(request.quantityAvailable());
        ticket.setQuantitySold(0);
        ticket.setSaleStart(request.saleStart());
        ticket.setSaleEnd(request.saleEnd());
        ticket.setStatus(TicketStatus.AVAILABLE);
        return ticket;
    }
}
